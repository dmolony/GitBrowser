package com.bytezone.gitbrowser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bytezone.gitbrowser.GitObject.ObjectType;

// -----------------------------------------------------------------------------------//
public class GitProject
// -----------------------------------------------------------------------------------//
{
  private static final int COMMIT = 1;
  private static final int TREE = 2;

  String projectName;

  File projectFolder;
  File objectsFolder;
  File packFolder;
  File headsFolder;

  int totalLooseObjects;
  int totalPackedObjects;

  private final List<PackFile> packFiles = new ArrayList<> ();
  private final Map<String, GitObject> objectsBySha = new TreeMap<> ();
  private final Map<String, File> filesBySha = new TreeMap<> ();

  // ---------------------------------------------------------------------------------//
  public GitProject (String projectPath)
  // ---------------------------------------------------------------------------------//
  {
    projectFolder = getMandatoryFile (projectPath);
    objectsFolder = getMandatoryFile (projectPath + "/.git/objects");
    packFolder = getOptionalFile (projectPath + "/.git/objects/pack");
    headsFolder = getMandatoryFile (projectPath + "/.git/refs/heads");

    projectName = projectFolder.getName ();

    for (File parentFolder : objectsFolder.listFiles ())
    {
      if (parentFolder.getName ().length () != 2)
        continue;

      File[] files = parentFolder.listFiles ();
      totalLooseObjects += files.length;

      for (File file : files)
      {
        String sha = parentFolder.getName () + file.getName ();
        filesBySha.put (sha, file);
      }
    }

    if (packFolder != null)
      addPackFiles ();
  }

  // ---------------------------------------------------------------------------------//
  void showHead ()
  // ---------------------------------------------------------------------------------//
  {
    for (File file : headsFolder.listFiles ())
    {
      System.out.printf ("%nBranch: %s%n%n", file.getName ());
      try
      {
        List<String> content = Files.readAllLines (file.toPath ());
        assert content.size () == 1;

        showCommit ((Commit) getObject (content.get (0)));
      }
      catch (IOException e)
      {
        e.printStackTrace ();
      }
    }
  }

  // ---------------------------------------------------------------------------------//
  private void showCommit (Commit commit)
  // ---------------------------------------------------------------------------------//
  {
    System.out.println (commit.getText ());
    showTree ((Tree) getObject (commit.getTreeSha ()));
  }

  // ---------------------------------------------------------------------------------//
  private void showTree (Tree tree)
  // ---------------------------------------------------------------------------------//
  {
    System.out.println ();
    System.out.println (getObject (tree.getSha ()).getText ());

    for (TreeItem treeItem : tree)
    {
      GitObject object = getObject (treeItem.sha1);
      if (object == null)
      {
        System.out.println ("*********** object not found *********");
      }
      else if (object.getObjectType () == ObjectType.TREE)
        showTree ((Tree) object);                               // recursion
    }
  }

  // ---------------------------------------------------------------------------------//
  public GitObject getObject (String sha)
  // ---------------------------------------------------------------------------------//
  {
    if (!objectsBySha.containsKey (sha))
      if (filesBySha.containsKey (sha))
      {
        GitObject object = GitObjectFactory.getObject (filesBySha.get (sha));
        objectsBySha.put (sha, object);
      }
      else
        System.out.printf ("SHA: %s not found%n", sha);

    return objectsBySha.get (sha);
  }

  // ---------------------------------------------------------------------------------//
  private GitObject getFloor (String sha)
  // ---------------------------------------------------------------------------------//
  {
    String shaHi = sha + "zz";
    String key = ((TreeMap<String, GitObject>) objectsBySha).floorKey (shaHi);

    if (key.startsWith (sha))
      return getObject (key);

    return getObject (((TreeMap<String, File>) filesBySha).floorKey (shaHi));
  }

  // Process .git/objects/pack folder
  // ---------------------------------------------------------------------------------//
  private void addPackFiles ()
  // ---------------------------------------------------------------------------------//
  {
    // process the index files first
    for (File file : packFolder.listFiles ())
      if (file.getName ().endsWith (".idx"))
        packFiles.add (new PackFile (file, objectsBySha));

    // add pack and reverse files
    for (File file : packFolder.listFiles ())
    {
      if (file.getName ().endsWith (".pack"))
        getPackFile (file).addPack (file);
      else if (file.getName ().endsWith (".rev"))
        getPackFile (file).addReverse (file);
      else if (!file.getName ().endsWith (".idx"))
        System.out.printf ("Unknown file : %s%n", file.getName ());
    }

    // create SHA mappings
    for (PackFile packFile : packFiles)
      for (PackFileItem packFileItem : packFile)
      {
        GitObject object = packFileItem.getObject ();
        objectsBySha.put (object.getSha (), object);
        ++totalPackedObjects;
      }
  }

  // ---------------------------------------------------------------------------------//
  private PackFile getPackFile (File file)
  // ---------------------------------------------------------------------------------//
  {
    for (PackFile packFile : packFiles)
      if (packFile.shaMatches (file))
        return packFile;

    return null;
  }

  // ---------------------------------------------------------------------------------//
  private File getOptionalFile (String path)
  // ---------------------------------------------------------------------------------//
  {
    File folder = new File (path);

    if (folder.exists ())
      return folder;

    return null;
  }

  // ---------------------------------------------------------------------------------//
  private File getMandatoryFile (String path)
  // ---------------------------------------------------------------------------------//
  {
    File folder = getOptionalFile (path);

    if (folder != null)
      return folder;

    System.out.println ("File not found: " + path);
    System.exit (0);

    return null;            // unreachable code, but the compiler sucks
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append ("Project name ............ %s%n".formatted (projectFolder.getName ()));
    text.append ("Loose objects ........... %,d%n".formatted (totalLooseObjects));
    text.append ("Pack files .............. %,d%n".formatted (packFiles.size ()));
    text.append ("Pack items .............. %,d%n".formatted (totalPackedObjects));

    return Utility.rtrim (text);
  }
}
