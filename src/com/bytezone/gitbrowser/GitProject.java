package com.bytezone.gitbrowser;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.gitbrowser.FileManager.Branch;
import com.bytezone.gitbrowser.FileManager.Remote;
import com.bytezone.gitbrowser.GitObject.ObjectType;

// -----------------------------------------------------------------------------------//
public class GitProject
// -----------------------------------------------------------------------------------//
{
  private String head;
  private String fullHead;
  private String fetchHead;

  private final FileManager fileManager;

  // ---------------------------------------------------------------------------------//
  public GitProject (String projectPath)
  // ---------------------------------------------------------------------------------//
  {
    fileManager = new FileManager (projectPath);

    fullHead = fileManager.getHead ();
    int pos = fullHead.lastIndexOf ('/');
    head = fullHead.substring (pos + 1);
    fetchHead = fileManager.getFetchHead ();
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
    for (Branch branch : fileManager.getBranches ())
      if (branch.name ().equals (head))
      {
        System.out.printf ("%nBranch: %s%n%n", branch.name ());
        Commit commit = (Commit) getObject (branch.sha ());
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
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append (
        "Project name ............ %s%n".formatted (fileManager.getProjectName ()));
    text.append ("Loose objects ........... %,d%n"
        .formatted (fileManager.getTotalLooseObjects ()));

    if (fileManager.getTotalPackedObjects () > 0)
    {
      text.append ("Pack files .............. %,d%n"
          .formatted (fileManager.getTotalPackedObjects ()));
      text.append ("Packed objects .......... %,d%n"
          .formatted (fileManager.getTotalPackedObjects ()));
    }

    List<Branch> branches = fileManager.getBranches ();
    text.append ("Branches ................ %,d%n".formatted (branches.size ()));

    for (Branch branch : branches)
    {
      String label = branch.name () + " .........................";
      text.append ("  %23.23s %6.6s%n".formatted (label, branch.sha ()));
    }

    List<Remote> remotes = fileManager.getRemotes ();
    if (remotes.size () > 0)
    {
      text.append ("Remotes ................. %,d%n".formatted (remotes.size ()));

      for (Remote remote : remotes)
      {
        String label = remote.name () + " .........................";
        text.append (
            "  %23.23s %6.6s %s%n".formatted (label, remote.sha (), remote.head ()));
      }
    }

    text.append ("HEAD .................... %s%n".formatted (fullHead));

    if (!fetchHead.isEmpty ())
      text.append ("FETCH_HEAD .............. %s%n".formatted (fetchHead));

    return Utility.rtrim (text);
  }
}
