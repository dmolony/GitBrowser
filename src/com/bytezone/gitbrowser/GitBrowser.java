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
  private static final int COMMIT = 1;
  private static final int TREE = 2;
  private static final int BLOB = 3;
  private static final int TAG = 4;
  int[] totals = new int[4];

  private final List<GitObject> objects = new ArrayList<> ();
  private final List<PackFile> packFiles = new ArrayList<> ();

  private final Map<String, GitObject> objectsBySha = new TreeMap<> ();
  private final Map<String, PackFileItem> packItemsBySha = new TreeMap<> ();

  // ---------------------------------------------------------------------------------//
  public GitBrowser () throws FileNotFoundException
  // ---------------------------------------------------------------------------------//
  {
    //    String project = "Common";
    //    String project = "DiskTest";
    //    String project = "EnigmaMachine";
    //    String project = "AppleFormat";
    //    String project = "GitBrowser";
    String project = "Plexer";
    //    String project = "VistaFob";
    //    String project = "Dataset";
    //    String project = "LoadLister";
    //    String project = "BreadBoard";

    String home = System.getProperty ("user.home");
    String path = home + "/Documents/GitLocal/" + project + "/.git/objects";
    File gitObjectsFolder = new File (path);

    if (!gitObjectsFolder.exists ())
    {
      System.out.println ("File not found: " + gitObjectsFolder.getAbsolutePath ());
      System.exit (0);
    }

    final Map<String, String> namesBySha = new TreeMap<> ();

    for (File parentFolder : gitObjectsFolder.listFiles ())
    {
      if (parentFolder.getName ().length () != 2)
        continue;

      for (File file : parentFolder.listFiles ())
      {
        GitObject object = GitObjectFactory.getObject (parentFolder, file);
        objects.add (object);
        objectsBySha.put (object.getSha (), object);
        totals[object.objectType.ordinal ()]++;

        // store file and folder names
        switch (object.getObjectType ())
        {
          case TREE:
            for (TreeItem treeItem : ((Tree) object))
              namesBySha.put (treeItem.sha1, treeItem.name);
            break;

          case COMMIT:
            Commit commit = (Commit) object;
            namesBySha.put (commit.getSha (), '"' + commit.getFirstMessageLine () + '"');
            namesBySha.put (commit.getTreeSha (), project);
            break;

          case BLOB:
          case TAG:
        }
      }
    }

    File packFolder = new File (path + "/pack");
    if (packFolder.exists ())
      addPackFiles (project, packFolder);

    // link name
    for (GitObject object : objectsBySha.values ())
      if (namesBySha.containsKey (object.getSha ()))
        object.setName (namesBySha.get (object.getSha ()));

    displayObjects (project);

    //    showCommit (114);           // GitBrowser
    //    showCommit (195);           // LoadLister
    //    showCommit (296);           // GitBrowser
    showCommit (14);           // Plexer

    //    displayObject ("245123c06d1b0a41f66e2763f7b3975601512c3b");
    //    for (int i = 1; i <= 6; i++)
    //      displayPackObject (0, i);
    //    displayPackObject (0, 11);
  }

  // ---------------------------------------------------------------------------------//
  private void showCommit (int index)
  // ---------------------------------------------------------------------------------//
  {
    if (index < 0 || index >= objects.size ())
    {
      System.out.printf ("Invalid index: %d%n", index);
      return;
    }

    GitObject commit = objects.get (index);
    if (commit.getObjectType () != ObjectType.COMMIT)
    {
      System.out.println ("Not a COMMIT");
      displayObject (index);
      return;
    }

    System.out.println (commit.getText ());
    showTree ((Tree) objectsBySha.get (((Commit) commit).getTreeSha ()));
  }

  // ---------------------------------------------------------------------------------//
  private void showTree (Tree tree)
  // ---------------------------------------------------------------------------------//
  {
    //    displayObject (tree.getSha ());
    System.out.println ();
    System.out.println (objectsBySha.get (tree.getSha ()).getText ());

    for (TreeItem treeItem : tree)
    {
      GitObject object = objectsBySha.get (treeItem.sha1);
      if (object == null)
      {
        PackFileItem packFileItem = packItemsBySha.get (treeItem.sha1);
        System.out.printf ("%6.6s  %-6s  %s%n", packFileItem.getSha (),
            packFileItem.getTypeText (), treeItem.name);
      }
      else if (object.getObjectType () == ObjectType.TREE)
        showTree ((Tree) object);                               // recursion
    }
  }

  // ---------------------------------------------------------------------------------//
  void displayObjects (String project)
  // ---------------------------------------------------------------------------------//
  {
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
      System.out.println ("Ndx  SHA-1   Type      Length  Filename/Folder/Message");
      System.out.println ("---  ------  ------  --------  ----------------------------");

      int count = 0;
      for (GitObject object : objects)
        System.out.printf ("%3d  %s%n", count++, object);
    }

    if (false)
      for (PackFile packFile : packFiles)
      {
        System.out.printf ("%nPack: %s%n%n", packFile.packFileSha1);
        displayPackTotals (packFile);
        System.out.println ();
        System.out.println ("Ndx  SHA-1   Type     Link      Length  Name");
        System.out.println (
            "---  ------  -------  ------  --------  -------------------------");

        int count = 0;
        for (PackFileItem packFileItem : packItemsBySha.values ())
          System.out.printf ("%3d  %s%n", count++, packFileItem);
      }

    System.out.println ();
  }

  // ---------------------------------------------------------------------------------//
  private void displayPackTotals (PackFile packFile)
  // ---------------------------------------------------------------------------------//
  {
    int[] totals = new int[8];

    for (PackFileItem packFileItem : packFile)
      totals[packFileItem.getType ()]++;

    System.out.printf ("Objects .... %,7d%n", packFile.totFiles);
    System.out.printf ("Commits .... %,7d%n", totals[1]);
    System.out.printf ("Trees ...... %,7d%n", totals[2]);
    System.out.printf ("Blobs ...... %,7d%n", totals[3]);
    System.out.printf ("Tags ....... %,7d%n", totals[4]);
    System.out.printf ("Delta ofs .. %,7d%n", totals[6]);
    System.out.printf ("Delta ref .. %,7d%n", totals[7]);
  }

  // ---------------------------------------------------------------------------------//
  private void displayObject (String sha)
  // ---------------------------------------------------------------------------------//
  {
    System.out.println ();
    System.out.println (objectsBySha.get (sha).getText ());
  }

  // ---------------------------------------------------------------------------------//
  private void displayObject (int index)
  // ---------------------------------------------------------------------------------//
  {
    System.out.println ();
    System.out.println (objects.get (index).getText ());
  }

  // ---------------------------------------------------------------------------------//
  private void displayPackObject (int packNo, int index)
  // ---------------------------------------------------------------------------------//
  {
    PackFileItem packFileItem = packFiles.get (packNo).getPackFileItem (index);
    GitObject object = packFileItem.getObject ();

    System.out.println ();
    System.out.println (object.getText ());
  }

  // Process .git/objects/pack folder
  // ---------------------------------------------------------------------------------//
  private void addPackFiles (String projectName, File parentFolder)
  // ---------------------------------------------------------------------------------//
  {
    // process the index files first
    for (File file : parentFolder.listFiles ())
      if (file.getName ().endsWith (".idx"))
        packFiles.add (new PackFile (file, objectsBySha));

    // add pack and reverse files
    for (File file : parentFolder.listFiles ())
    {
      if (file.getName ().endsWith (".pack"))
        getPackFile (file).addPack (file);
      else if (file.getName ().endsWith (".rev"))
        getPackFile (file).addReverse (file);
      else if (!file.getName ().endsWith (".idx"))
        System.out.printf ("Unknown file : %s%n", file.getName ());
    }

    final Map<String, String> packNamesBySha = new TreeMap<> ();

    // create SHA mappings
    for (PackFile packFile : packFiles)
      for (PackFileItem packFileItem : packFile)
      {
        packItemsBySha.put (packFileItem.getSha (), packFileItem);
        GitObject object = packFileItem.getObject ();

        objects.add (object);
        objectsBySha.put (object.getSha (), object);
        totals[object.objectType.ordinal ()]++;

        switch (packFileItem.getBaseType ())
        {
          case TREE:
            for (TreeItem treeItem : (Tree) object)
              packNamesBySha.put (treeItem.sha1, treeItem.name);
            break;

          case COMMIT:
            Commit commit = (Commit) object;
            packNamesBySha.put (commit.getSha (),
                '"' + commit.getFirstMessageLine () + '"');
            packNamesBySha.put (commit.getTreeSha (), projectName);
            break;
        }
      }

    // link names
    for (PackFileItem packFileItem : packItemsBySha.values ())
    {
      GitObject object = packFileItem.getObject ();
      if (packNamesBySha.containsKey (object.getSha ()))
        object.setName (packNamesBySha.get (object.getSha ()));
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
  public static void main (String[] args) throws FileNotFoundException
  // ---------------------------------------------------------------------------------//
  {
    new GitBrowser ();
  }
}
