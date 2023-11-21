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
  private final File projectFolder;
  private final File objectsFolder;
  private final File packFolder;
  private final File headsFolder;
  private final File remotesFolder;

  private int totalPackedObjects;
  private String head;
  private String fullHead;
  private String fetchHead;

  private final List<PackFile> packFiles = new ArrayList<> ();
  private final TreeMap<String, GitObject> objectsBySha = new TreeMap<> ();
  private final TreeMap<String, File> filesBySha = new TreeMap<> ();
  private final List<Branch> branches = new ArrayList<> ();
  private final List<Remote> remotes = new ArrayList<> ();

  // ---------------------------------------------------------------------------------//
  public GitProject (String projectPath)
  // ---------------------------------------------------------------------------------//
  {
    projectFolder = getMandatoryFile (projectPath);
    objectsFolder = getMandatoryFile (projectPath + "/.git/objects");
    headsFolder = getMandatoryFile (projectPath + "/.git/refs/heads");

    packFolder = getOptionalFile (projectPath + "/.git/objects/pack");
    remotesFolder = getOptionalFile (projectPath + "/.git/refs/remotes");

    addFiles ();

    if (packFolder != null)
      addPackFiles ();

    fullHead = getHead ();
    int pos = fullHead.lastIndexOf ('/');
    head = fullHead.substring (pos + 1);
    fetchHead = getFetchHead ();
  }

  // ---------------------------------------------------------------------------------//
  public String getHead ()
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      List<String> content =
          Files.readAllLines (new File (projectFolder + "/.git/HEAD").toPath ());
      String line = content.get (0);
      return line;
    }
    catch (IOException e)
    {
      e.printStackTrace ();
      return "";
    }
  }

  // ---------------------------------------------------------------------------------//
  public String getFetchHead ()
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      File fetchHeadFile = new File (projectFolder + "/.git/FETCH_HEAD");
      if (!fetchHeadFile.exists ())
        return "";

      List<String> content = Files.readAllLines (fetchHeadFile.toPath ());
      String line = content.get (0);

      return line;
    }
    catch (IOException e)
    {
      e.printStackTrace ();
      return "";
    }
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
        objectsBySha.put (sha, GitObjectFactory.getObject (filesBySha.get (sha)));
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
      List<String> parents = commit.getParentShas ();
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
    for (Branch branch : branches)
      if (branch.name.equals (head))
      {
        System.out.printf ("%nBranch: %s%n%n", branch.name);
        Commit commit = (Commit) getObject (branch.sha);
        showCommitChain (commit);
        System.out.println ();
        showCommit (commit);
        break;
      }
  }

  // ---------------------------------------------------------------------------------//
  //  public void showCommit (String sha)
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    GitObject commit = getObject (sha);
  //    if (commit.objectType != ObjectType.COMMIT)
  //    {
  //      System.out.println ("Not a COMMIT");
  //      return;
  //    }
  //
  //    System.out.println (commit.getText ());
  //    showTree ((Tree) getObject (((Commit) commit).getTreeSha ()));
  //  }

  // ---------------------------------------------------------------------------------//
  private void showCommit (Commit commit)
  // ---------------------------------------------------------------------------------//
  {
    System.out.println (commit.getText ());
    List<String> shaList = new ArrayList<> ();

    // build list of shas in the parent commits
    for (String parentSha : commit.getParentShas ())
    {
      Commit previousCommit = (Commit) getObject (parentSha);
      addTreeShas ((Tree) getObject (previousCommit.getTreeSha ()), shaList);
    }

    showTreeWithPrevious ((Tree) getObject (commit.getTreeSha ()), shaList);
  }

  // walk the commit tree
  // ---------------------------------------------------------------------------------//
  //  private void showTree (Tree tree)
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    System.out.println ();
  //    System.out.println (getObject (tree.getSha ()).getText ());
  //
  //    for (TreeItem treeItem : tree)
  //    {
  //      GitObject object = getObject (treeItem.sha);
  //      if (object == null)
  //        System.out.println ("*********** object not found *********");
  //      else if (object.getObjectType () == ObjectType.TREE)
  //        showTree ((Tree) object);                               // recursion
  //    }
  //  }

  // walk the commit tree
  // ---------------------------------------------------------------------------------//
  private void showTreeWithPrevious (Tree tree, List<String> shaList)
  // ---------------------------------------------------------------------------------//
  {
    System.out.println ();
    System.out.println (tree.getText (shaList));

    for (TreeItem treeItem : tree)
    {
      GitObject object = getObject (treeItem.sha);
      if (object == null)
        System.out.println ("*********** object not found *********");
      else if (object.getObjectType () == ObjectType.TREE)
        showTreeWithPrevious ((Tree) object, shaList);           // recursion
    }
  }

  // build list of shas used by the previous commit
  // ---------------------------------------------------------------------------------//
  void addTreeShas (Tree tree, List<String> shaList)
  // ---------------------------------------------------------------------------------//
  {
    for (TreeItem treeItem : tree)
    {
      shaList.add (treeItem.sha);
      GitObject object = getObject (treeItem.sha);
      if (object == null)
        System.out.println ("*********** object not found *********");
      else if (object.getObjectType () == ObjectType.TREE)
        addTreeShas ((Tree) object, shaList);                    // recursion
    }
  }

  // ---------------------------------------------------------------------------------//
  private void addFiles ()
  // ---------------------------------------------------------------------------------//
  {
    // loose objects
    for (File parentFolder : objectsFolder.listFiles ())
      if (parentFolder.getName ().length () == 2)
        for (File file : parentFolder.listFiles ())
          filesBySha.put (parentFolder.getName () + file.getName (), file);

    try
    {
      // remotes
      if (remotesFolder != null)
        for (File folder : remotesFolder.listFiles ())
          for (File file : folder.listFiles ())
            for (String content : Files.readAllLines (file.toPath ()))
              remotes.add (new Remote (folder.getName (), file.getName (), content));

      // branches
      for (File file : headsFolder.listFiles ())
        for (String content : Files.readAllLines (file.toPath ()))
          branches.add (new Branch (file.getName (), content));
    }
    catch (IOException e)
    {
      e.printStackTrace ();
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
      if (file.getName ().endsWith (".pack"))
        getPackFile (file).addPack (file);
      else if (file.getName ().endsWith (".rev"))
        getPackFile (file).addReverse (file);
      else if (!file.getName ().endsWith (".idx"))
        System.out.printf ("Unknown file : %s%n", file.getName ());

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
  record Branch (String name, String sha)
  // ---------------------------------------------------------------------------------//
  {
  };

  // ---------------------------------------------------------------------------------//
  record Remote (String name, String head, String sha)
  // ---------------------------------------------------------------------------------//
  {
  };

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append ("Project name ............ %s%n".formatted (projectFolder.getName ()));
    text.append ("Loose objects ........... %,d%n".formatted (filesBySha.size ()));

    if (packFiles.size () > 0)
    {
      text.append ("Pack files .............. %,d%n".formatted (packFiles.size ()));
      text.append ("Packed objects .......... %,d%n".formatted (totalPackedObjects));
    }

    text.append ("Branches ................ %,d%n".formatted (branches.size ()));

    for (Branch branch : branches)
    {
      String label = branch.name + " .........................";
      text.append ("  %23.23s %6.6s%n".formatted (label, branch.sha));
    }

    if (remotes.size () > 0)
    {
      text.append ("Remotes ................. %,d%n".formatted (remotes.size ()));

      for (Remote remote : remotes)
      {
        String label = remote.name + " .........................";
        text.append ("  %23.23s %6.6s %s%n".formatted (label, remote.sha, remote.head));
      }
    }

    text.append ("HEAD .................... %s%n".formatted (fullHead));

    if (!fetchHead.isEmpty ())
      text.append ("FETCH_HEAD .............. %s%n".formatted (fetchHead));

    return Utility.rtrim (text);
  }
}
