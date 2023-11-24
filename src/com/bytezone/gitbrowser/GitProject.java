package com.bytezone.gitbrowser;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.gitbrowser.FileManager.Branch;
import com.bytezone.gitbrowser.GitObject.ObjectType;

// -----------------------------------------------------------------------------------//
public class GitProject
// -----------------------------------------------------------------------------------//
{
  private final FileManager fileManager;

  // ---------------------------------------------------------------------------------//
  public GitProject (String projectPath)
  // ---------------------------------------------------------------------------------//
  {
    fileManager = new DefaultFileManager (projectPath);
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
    Branch branch = fileManager.getCurrentBranch ();
    if (branch != null)
    {
      System.out.printf ("%nBranch: %s%n%n", branch.name ());
      Commit commit = (Commit) getObject (branch.sha ());
      showCommitChain (commit);
      System.out.println ();
      showCommit (commit);
    }
  }

  // ---------------------------------------------------------------------------------//
  void showCommit (String sha)
  // ---------------------------------------------------------------------------------//
  {
    GitObject object = getObject (sha);
    if (object instanceof Commit commit)
      showCommit (commit);
    else
      System.out.printf ("Not a commit: %s%n", sha);
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
    return fileManager.toString ();
  }
}
