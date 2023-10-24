package com.bytezone.gitbrowser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bytezone.gitbrowser.GitObject.ObjectType;

// -----------------------------------------------------------------------------------//
public class GitBrowser
// -----------------------------------------------------------------------------------//
{
  private final List<GitObject> objects = new ArrayList<> ();
  private final List<PackFile> packFiles = new ArrayList<> ();
  private final Map<String, GitObject> objectsBySha = new TreeMap<> ();
  private final Map<String, String> namesBySha = new TreeMap<> ();

  // ---------------------------------------------------------------------------------//
  public GitBrowser () throws FileNotFoundException
  // ---------------------------------------------------------------------------------//
  {
    //    String project = "Common";
    //    String project = "DiskTest";
    //    String project = "EnigmaMachine";
    //    String project = "AppleFormat";
    String project = "GitBrowser";

    String home = System.getProperty ("user.home");
    String path = home + "/Documents/GitLocal/" + project + "/.git/objects";
    File gitObjectsFolder = new File (path);

    for (File parentFolder : gitObjectsFolder.listFiles ())
      if (parentFolder.getName ().length () == 2)
        for (File file : parentFolder.listFiles ())
        {
          GitObject object = GitObjectFactory.getObject (parentFolder, file);
          objects.add (object);
          objectsBySha.put (object.getSha (), object);

          if (object.getObjectType () == ObjectType.TREE)
            doTree ((Tree) object);
          else if (object.getObjectType () == ObjectType.COMMIT)
            doCommit ((Commit) object);
        }

    File packFolder = new File (path + "/pack");
    if (packFolder.exists ())
      addPackFiles (packFolder);

    displayTotals (project);

    //    displayObject (0);
    //    displayObject ("245123c06d1b0a41f66e2763f7b3975601512c3b");
    //    for (int i = 1; i <= 6; i++)
    //      displayPackObject (0, i);
    displayPackObject (0, 11);
  }

  // ---------------------------------------------------------------------------------//
  void displayTotals (String project)
  // ---------------------------------------------------------------------------------//
  {
    int[] totals = new int[4];

    for (GitObject gitObject : objects)
      totals[gitObject.objectType.ordinal ()]++;

    System.out.printf ("Project .... %s%n%n", project);
    System.out.printf ("Objects .... %,7d%n", objects.size ());
    System.out.printf ("Commits .... %,7d%n", totals[0]);
    System.out.printf ("Trees ...... %,7d%n", totals[1]);
    System.out.printf ("Blobs ...... %,7d%n", totals[2]);
    System.out.printf ("Tags ....... %,7d%n", totals[3]);
    System.out.printf ("Packfiles .. %,7d%n", packFiles.size ());

    if (true)
    {
      System.out.println ();
      System.out.println ("Ndx  SHA-1   Type      Length");
      System.out.println ("---  ------  ------  --------");

      for (int i = 0; i < objects.size (); i++)
      {
        GitObject object = objects.get (i);
        String name = namesBySha.get (object.getSha ().toUpperCase ());
        System.out.printf ("%3d  %s  %s%n", i, object,
            object.getObjectType () == ObjectType.BLOB ? name : "");
      }
    }

    if (true)
      for (PackFile packFile : packFiles)
      {
        System.out.printf ("%nPack: %s%n%n", packFile.packFileSha1);
        displayPackTotals (packFile);
        System.out.println ();
        System.out.println (
            "Ndx  SHA-1   Type      Offset  DstSize  RefOfst  SrcSize  DstSize");
        System.out.println (
            "---  ------  -------  -------  -------  -------  -------  -------");

        for (int i = 0; i < packFile.totFiles; i++)
          System.out.printf ("%3d  %s%n", i, packFile.packFileItems.get (i));
      }
  }

  // ---------------------------------------------------------------------------------//
  private void displayPackTotals (PackFile packFile)
  // ---------------------------------------------------------------------------------//
  {
    int[] totals = new int[8];

    for (int i = 0; i < packFile.totFiles; i++)
      totals[packFile.packFileItems.get (i).getType ()]++;

    System.out.printf ("Objects .... %,7d%n", packFile.totFiles);
    System.out.printf ("Commits .... %,7d%n", totals[1]);
    System.out.printf ("Trees ...... %,7d%n", totals[2]);
    System.out.printf ("Blobs ...... %,7d%n", totals[3]);
    System.out.printf ("Tags ....... %,7d%n", totals[4]);
    System.out.printf ("Delta ofs .. %,7d%n", totals[6]);
    System.out.printf ("Delta ref .. %,7d%n", totals[7]);
  }

  // ---------------------------------------------------------------------------------//
  void displayObject (String sha)
  // ---------------------------------------------------------------------------------//
  {
    System.out.println ();
    System.out.println (objectsBySha.get (sha).getText ());
  }

  // ---------------------------------------------------------------------------------//
  void displayObject (int index)
  // ---------------------------------------------------------------------------------//
  {
    System.out.println ();
    System.out.println (objects.get (index).getText ());
  }

  // ---------------------------------------------------------------------------------//
  void displayPackObject (int packNo, int index)
  // ---------------------------------------------------------------------------------//
  {
    PackFileItem packFileItem = packFiles.get (packNo).packFileItems.get (index);

    System.out.println ();
    GitObject object = packFileItem.getObject ();
    //    System.out.println (object);
    System.out.println (object.getText ());
  }

  // Process .git/objects/pack folder
  // ---------------------------------------------------------------------------------//
  private void addPackFiles (File parentFolder)
  // ---------------------------------------------------------------------------------//
  {
    // process the index files first
    for (File file : parentFolder.listFiles ())
      if (file.getName ().endsWith (".idx"))
        packFiles.add (new PackFile (file, objectsBySha));

    for (File file : parentFolder.listFiles ())
    {
      if (file.getName ().endsWith (".pack"))
        getPackFile (file).addPack (file);
      else if (file.getName ().endsWith (".rev"))
        getPackFile (file).addReverse (file);
      else if (!file.getName ().endsWith (".idx"))
        System.out.printf ("Unknown file : %s%n", file.getName ());
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
  private void doTree (Tree tree)
  // ---------------------------------------------------------------------------------//
  {
    for (TreeItem treeItem : tree)
      namesBySha.put (treeItem.sha1, treeItem.name);
  }

  // ---------------------------------------------------------------------------------//
  private void doCommit (Commit commit)
  // ---------------------------------------------------------------------------------//
  {
    namesBySha.put (commit.getTreeSha (), "root");
  }

  // ---------------------------------------------------------------------------------//
  public static void main (String[] args) throws FileNotFoundException
  // ---------------------------------------------------------------------------------//
  {
    new GitBrowser ();
  }
}
