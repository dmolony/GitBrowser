package com.bytezone.gitbrowser;

import java.util.List;

import com.bytezone.gitbrowser.DefaultFileManager.Branch;
import com.bytezone.gitbrowser.DefaultFileManager.Remote;

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
}
