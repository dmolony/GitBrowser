package com.bytezone.gitbrowser;

import java.util.List;

// -----------------------------------------------------------------------------------//
public interface FileManager
{
  public GitObject getObject (String sha);

  public String getProjectName ();

  public String getHead ();

  public List<Branch> getBranches ();

  public List<Remote> getRemotes ();

  public int getTotalLooseObjects ();

  public int getTotalPackedObjects ();

  public int getTotalPackFiles ();

  public Branch getCurrentBranch ();

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
}
