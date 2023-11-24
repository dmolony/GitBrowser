package com.bytezone.gitbrowser;

import java.util.List;

import com.bytezone.gitbrowser.FileManager.Branch;
import com.bytezone.gitbrowser.FileManager.Remote;

// -----------------------------------------------------------------------------------//
public interface FileStuff
// -----------------------------------------------------------------------------------//
{
  public GitObject getObject (String sha);

  public String getProjectName ();

  public String getHead ();

  public List<Branch> getBranches ();

  public List<Remote> getRemotes ();

  public int getTotalLooseObjects ();

  public int getTotalPackedObjects ();

  public int getTotalPackFiles ();
}
