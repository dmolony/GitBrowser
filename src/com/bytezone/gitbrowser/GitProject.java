package com.bytezone.gitbrowser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.bytezone.gitbrowser.GitObject.ObjectType;

// -----------------------------------------------------------------------------------//
public class GitProject
// -----------------------------------------------------------------------------------//
{
  String projectName;

  File projectFolder;
  File objectsFolder;
  File packFolder;
  File headsFolder;

  int totalLooseObjects;
  int totalPackedObjects;

  private final List<PackFile> packFiles = new ArrayList<> ();
  private final TreeMap<String, GitObject> objectsBySha = new TreeMap<> ();
  private final TreeMap<String, File> filesBySha = new TreeMap<> ();

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
  public GitObject getObject (String sha)
  // ---------------------------------------------------------------------------------//
  {
    if (sha.length () < 40)
    {
      String shaHi = sha + "zz";
      String key = objectsBySha.floorKey (shaHi);
      sha = key.startsWith (sha) ? key : filesBySha.floorKey (shaHi);
    }

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
  void showCommitChain (Commit commit)
  // ---------------------------------------------------------------------------------//
  {
    while (commit != null)
    {
      System.out.println (commit);
      List<String> parents = commit.getParents ();
      if (parents.size () == 0)
        break;
      commit = (Commit) getObject (parents.get (0));    // not sure about merges
    }
  }

  // ---------------------------------------------------------------------------------//
  void showObject (String sha)
  // ---------------------------------------------------------------------------------//
  {
    System.out.println (getObject (sha).getText ());
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

        showCommitChain ((Commit) getObject (content.get (0)));
        System.out.println ();
        showCommit ((Commit) getObject (content.get (0)));
      }
      catch (IOException e)
      {
        e.printStackTrace ();
      }
    }
  }

  // ---------------------------------------------------------------------------------//
  public void showCommit (String sha)
  // ---------------------------------------------------------------------------------//
  {
    GitObject commit = getObject (sha);
    if (commit.objectType != ObjectType.COMMIT)
    {
      System.out.println ("Not a COMMIT");
      return;
    }

    System.out.println (commit.getText ());
    showTree ((Tree) getObject (((Commit) commit).getTreeSha ()));
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
      GitObject object = getObject (treeItem.sha);
      if (object == null)
        System.out.println ("*********** object not found *********");
      else if (object.getObjectType () == ObjectType.TREE)
        showTree ((Tree) object);                               // recursion
    }
  }

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
    text.append ("Packed objects .......... %,d%n".formatted (totalPackedObjects));

    return Utility.rtrim (text);
  }
}
