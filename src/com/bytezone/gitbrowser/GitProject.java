package com.bytezone.gitbrowser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.bytezone.gitbrowser.GitObject.ObjectType;

// -----------------------------------------------------------------------------------//
public class GitProject
// -----------------------------------------------------------------------------------//
{
  private final File projectFolder;
  private final File headsFolder;
  private final File remotesFolder;

  private String head;
  private String fullHead;
  private String fetchHead;

  private final FileManager fileManager;

  private final List<Branch> branches = new ArrayList<> ();
  private final List<Remote> remotes = new ArrayList<> ();

  // ---------------------------------------------------------------------------------//
  public GitProject (String projectPath)
  // ---------------------------------------------------------------------------------//
  {
    projectFolder = getMandatoryFile (projectPath);
    headsFolder = getMandatoryFile (projectPath + "/.git/refs/heads");
    remotesFolder = getOptionalFile (projectPath + "/.git/refs/remotes");

    fileManager = new FileManager (projectPath);

    addFiles ();

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
    return fileManager.getObject (sha);
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

    showTree ((Tree) getObject (commit.getTreeSha ()), shaList);
  }

  // walk the commit tree
  // ---------------------------------------------------------------------------------//
  private void showTree (Tree tree, List<String> shaList)
  // ---------------------------------------------------------------------------------//
  {
    System.out.println ();
    System.out.println (tree.getText (shaList));

    for (TreeItem treeItem : tree)
    {
      GitObject object = getObject (treeItem.sha);
      if (object == null)
        System.out.printf ("Not found: %s%n", treeItem.sha);
      else if (object.getObjectType () == ObjectType.TREE)
        showTree ((Tree) object, shaList);                        // recursion
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
        System.out.printf ("Not found: %s%n", treeItem.sha);
      else if (object.getObjectType () == ObjectType.TREE)
        addTreeShas ((Tree) object, shaList);                    // recursion
    }
  }

  // ---------------------------------------------------------------------------------//
  private void addFiles ()
  // ---------------------------------------------------------------------------------//
  {
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
  private File getOptionalFile (String path)
  // ---------------------------------------------------------------------------------//
  {
    File folder = new File (path);

    return folder.exists () ? folder : null;
  }

  // ---------------------------------------------------------------------------------//
  private File getMandatoryFile (String path)
  // ---------------------------------------------------------------------------------//
  {
    File folder = getOptionalFile (path);

    if (folder == null)
    {
      System.out.println ("File not found: " + path);
      System.exit (0);
    }

    return folder;
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
    text.append ("Loose objects ........... %,d%n"
        .formatted (fileManager.getTotalLooseObjects ()));

    if (fileManager.getTotalPackedObjects () > 0)
    {
      text.append ("Pack files .............. %,d%n"
          .formatted (fileManager.getTotalPackedObjects ()));
      text.append ("Packed objects .......... %,d%n"
          .formatted (fileManager.getTotalPackedObjects ()));
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
