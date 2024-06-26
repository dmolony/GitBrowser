package com.bytezone.gitbrowser;

import java.io.FileNotFoundException;

// -----------------------------------------------------------------------------------//
public class GitBrowser
// -----------------------------------------------------------------------------------//
{
  String gitFolderName = System.getProperty ("user.home") + "/Documents/GitLocal/";
  //  String gitFolderName = System.getProperty ("user.home") + "/code/git/";

  // ---------------------------------------------------------------------------------//
  public GitBrowser () throws FileNotFoundException
  // ---------------------------------------------------------------------------------//
  {
    //    String project = "Common";
    //    String project = "DiskTest";
    //    String project = "EnigmaMachine";
    //    String project = "AppleFormat";
    //    String project = "GitBrowser";
    String project = "Scroller";
    //    String project = "Plexer";
    //    String project = "VistaFob";
    //    String project = "Dataset";
    //    String project = "LoadLister";
    //    String project = "BreadBoard";
    //    String project = "branch-example";
    //    String project = "alpha";

    GitProject gitProject = new GitProject (gitFolderName + project);

    System.out.println (gitProject);
    gitProject.showHead ();
    //    System.out.println ();
    //    gitProject.showObject ("295cec");
    //    gitProject.showCommit ("b7981c");
  }

  // ---------------------------------------------------------------------------------//
  public static void main (String[] args) throws FileNotFoundException
  // ---------------------------------------------------------------------------------//
  {
    new GitBrowser ();
  }
}
