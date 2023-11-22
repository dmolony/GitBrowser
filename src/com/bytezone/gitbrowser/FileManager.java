package com.bytezone.gitbrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

// -----------------------------------------------------------------------------------//
public class FileManager
// -----------------------------------------------------------------------------------//
{
  private final TreeMap<String, GitObject> objectsBySha = new TreeMap<> ();
  private final TreeMap<String, File> filesBySha = new TreeMap<> ();
  private final List<PackFile> packFiles = new ArrayList<> ();

  private int totalPackedObjects;

  private File objectsFolder;
  private File packFolder;

  // ---------------------------------------------------------------------------------//
  public FileManager (String projectPath)
  // ---------------------------------------------------------------------------------//
  {
    objectsFolder = getMandatoryFile (projectPath + "/.git/objects");
    packFolder = getOptionalFile (projectPath + "/.git/objects/pack");

    addFiles ();

    if (packFolder != null)
      addPackFiles ();
  }

  // ---------------------------------------------------------------------------------//
  public GitObject getObject (String sha)
  // ---------------------------------------------------------------------------------//
  {
    if (sha.length () < 40)
    {
      String shaHi = sha + "zz";
      String key = objectsBySha.floorKey (shaHi);
      sha = key.startsWith (sha) ? key : filesBySha.floorKey (shaHi);
    }

    if (!objectsBySha.containsKey (sha))
      if (filesBySha.containsKey (sha))
        objectsBySha.put (sha, GitObjectFactory.getObject (filesBySha.get (sha)));
      else
        System.out.printf ("SHA: %s not found%n", sha);

    return objectsBySha.get (sha);
  }

  // ---------------------------------------------------------------------------------//
  public int getTotalLooseObjects ()
  // ---------------------------------------------------------------------------------//
  {
    return filesBySha.size ();
  }

  // ---------------------------------------------------------------------------------//
  public int getTotalPackedObjects ()
  // ---------------------------------------------------------------------------------//
  {
    return totalPackedObjects;
  }

  // ---------------------------------------------------------------------------------//
  public int getTotalPackFiles ()
  // ---------------------------------------------------------------------------------//
  {
    return packFiles.size ();
  }

  // ---------------------------------------------------------------------------------//
  private void addFiles ()
  // ---------------------------------------------------------------------------------//
  {
    for (File parentFolder : objectsFolder.listFiles ())
      if (parentFolder.getName ().length () == 2)
        for (File file : parentFolder.listFiles ())
          filesBySha.put (parentFolder.getName () + file.getName (), file);
  }

  // ---------------------------------------------------------------------------------//
  private void addPackFiles ()
  // ---------------------------------------------------------------------------------//
  {
    // process the index files first
    for (File file : packFolder.listFiles ())
      if (file.getName ().endsWith (".idx"))
        packFiles.add (new PackFile (file, objectsBySha));

    // add pack and reverse files
    for (File file : packFolder.listFiles ())
      if (file.getName ().endsWith (".pack"))
        getPackFile (file).addPack (file);
      else if (file.getName ().endsWith (".rev"))
        getPackFile (file).addReverse (file);
      else if (!file.getName ().endsWith (".idx"))
        System.out.printf ("Unknown file : %s%n", file.getName ());

    // create SHA mappings
    for (PackFile packFile : packFiles)
      for (PackFileItem packFileItem : packFile)
      {
        GitObject object = packFileItem.getObject ();
        objectsBySha.put (object.getSha (), object);
        ++totalPackedObjects;
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
  private File getOptionalFile (String path)
  // ---------------------------------------------------------------------------------//
  {
    File folder = new File (path);

    return folder.exists () ? folder : null;
  }

  // ---------------------------------------------------------------------------------//
  private File getMandatoryFile (String path)
  // ---------------------------------------------------------------------------------//
  {
    File folder = getOptionalFile (path);

    if (folder == null)
    {
      System.out.println ("File not found: " + path);
      System.exit (0);
    }

    return folder;
  }
}
