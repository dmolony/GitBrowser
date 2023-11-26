package com.bytezone.gitbrowser;

import java.util.List;

// -----------------------------------------------------------------------------------//
public interface FileManager
{
  public GitObject getObject (String sha);

  public String projectName ();

  public String head ();

  public List<Branch> branches ();

  public List<Remote> remotes ();

  public int totalLooseObjects ();

  public int totalPackedObjects ();

  public int totalPackFiles ();

  public Branch currentBranch ();

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
