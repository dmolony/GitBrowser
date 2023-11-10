package com.bytezone.gitbrowser;

import java.io.FileNotFoundException;

// -----------------------------------------------------------------------------------//
public class GitBrowser
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public GitBrowser () throws FileNotFoundException
  // ---------------------------------------------------------------------------------//
  {
    //    String project = "Common";
    //    String project = "DiskTest";
    //    String project = "EnigmaMachine";
    //    String project = "AppleFormat";
    //    String project = "GitBrowser";
    //    String project = "Plexer";
    String project = "VistaFob";
    //    String project = "Dataset";
    //    String project = "LoadLister";
    //    String project = "BreadBoard";

    String home = System.getProperty ("user.home");

    GitProject gitProject = new GitProject (home + "/Documents/GitLocal/" + project);
    System.out.println (gitProject);

    gitProject.showHead ();
  }

  // ---------------------------------------------------------------------------------//
  public static void main (String[] args) throws FileNotFoundException
  // ---------------------------------------------------------------------------------//
  {
    new GitBrowser ();
  }
}
