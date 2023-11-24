package com.bytezone.gitbrowser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

//https://www.alibabacloud.com/blog/597391
// -----------------------------------------------------------------------------------//
public class FileManager implements FileStuff
// -----------------------------------------------------------------------------------//
{
  private byte[] buffer = new byte[0x10000];

  private final String head;
  private final String fullHead;
  private final String fetchHead;

  private final TreeMap<String, GitObject> objectsBySha = new TreeMap<> ();
  private final TreeMap<String, File> filesBySha = new TreeMap<> ();
  private final List<PackFile> packFiles = new ArrayList<> ();

  private int totalPackedObjects;

  private final File projectFolder;
  private final File objectsFolder;
  private final File packFolder;
  private final File headsFolder;
  private final File remotesFolder;

  private final List<Branch> branches = new ArrayList<> ();
  private final List<Remote> remotes = new ArrayList<> ();

  // ---------------------------------------------------------------------------------//
  public FileManager (String projectPath)
  // ---------------------------------------------------------------------------------//
  {
    projectFolder = getMandatoryFile (projectPath);
    objectsFolder = getMandatoryFile (projectPath + "/.git/objects");
    packFolder = getOptionalFile (projectPath + "/.git/objects/pack");
    headsFolder = getMandatoryFile (projectPath + "/.git/refs/heads");
    remotesFolder = getOptionalFile (projectPath + "/.git/refs/remotes");

    addFiles ();

    if (packFolder != null)
      addPackFiles ();

    fullHead = getFullHead ();
    int pos = fullHead.lastIndexOf ('/');
    head = fullHead.substring (pos + 1);
    fetchHead = getFetchHead ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
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
        objectsBySha.put (sha, getObject (filesBySha.get (sha)));
      else
        System.out.printf ("SHA: %s not found%n", sha);

    return objectsBySha.get (sha);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getProjectName ()
  // ---------------------------------------------------------------------------------//
  {
    return projectFolder.getName ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getHead ()
  // ---------------------------------------------------------------------------------//
  {
    return head;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int getTotalLooseObjects ()
  // ---------------------------------------------------------------------------------//
  {
    return filesBySha.size ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int getTotalPackedObjects ()
  // ---------------------------------------------------------------------------------//
  {
    return totalPackedObjects;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int getTotalPackFiles ()
  // ---------------------------------------------------------------------------------//
  {
    return packFiles.size ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public List<Branch> getBranches ()
  // ---------------------------------------------------------------------------------//
  {
    return branches;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public List<Remote> getRemotes ()
  // ---------------------------------------------------------------------------------//
  {
    return remotes;
  }

  // ---------------------------------------------------------------------------------//
  private void addFiles ()
  // ---------------------------------------------------------------------------------//
  {
    // loose objects
    for (File parentFolder : objectsFolder.listFiles ())
      if (parentFolder.getName ().length () == 2)
        for (File file : parentFolder.listFiles ())
          filesBySha.put (parentFolder.getName () + file.getName (), file);

    try
    {
      // remotes
      if (remotesFolder != null)
        for (File folder : remotesFolder.listFiles ())
          for (File file : folder.listFiles ())
            for (String content : Files.readAllLines (file.toPath ()))
              remotes.add (new Remote (folder.getName (), file.getName (), content));

      // branches
      for (File file : headsFolder.listFiles ())
        for (String content : Files.readAllLines (file.toPath ()))
          branches.add (new Branch (file.getName (), content));
    }
    catch (IOException e)
    {
      e.printStackTrace ();
    }
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
  private String getFullHead ()
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      List<String> content =
          Files.readAllLines (new File (projectFolder + "/.git/HEAD").toPath ());
      return content.get (0);
    }
    catch (IOException e)
    {
      e.printStackTrace ();
      return "";
    }
  }

  // ---------------------------------------------------------------------------------//
  private String getFetchHead ()
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      File fetchHeadFile = new File (projectFolder + "/.git/FETCH_HEAD");
      if (!fetchHeadFile.exists ())
        return "";

      List<String> content = Files.readAllLines (fetchHeadFile.toPath ());
      return content.get (0);
    }
    catch (IOException e)
    {
      e.printStackTrace ();
      return "";
    }
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

  // ---------------------------------------------------------------------------------//
  private GitObject getObject (File file)
  // ---------------------------------------------------------------------------------//
  {
    String sha = file.getParentFile ().getName () + file.getName ();

    try
    {
      byte[] content = Files.readAllBytes (file.toPath ());

      Inflater decompresser = new Inflater ();
      decompresser.setInput (content, 0, content.length);
      int resultLength = decompresser.inflate (buffer);
      decompresser.end ();

      int ptr = 0;
      while (buffer[ptr++] != 0)            // find the first null
        ;

      String type = new String (buffer, 0, ptr - 1, "UTF-8");
      String[] chunks = type.split (" ");
      int dataLength = Integer.parseInt (chunks[1]);

      byte[] data = new byte[dataLength];
      System.arraycopy (buffer, resultLength - dataLength, data, 0, dataLength);

      return switch (chunks[0])
      {
        case "blob" -> new Blob (sha, data);
        case "tree" -> new Tree (sha, data);
        case "commit" -> new Commit (sha, data);
        case "tag" -> new Tag (sha, data);
        default -> null;
      };
    }
    catch (IOException | DataFormatException e)
    {
      e.printStackTrace ();
      return null;
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append ("Project name ............ %s%n".formatted (getProjectName ()));
    text.append ("Loose objects ........... %,d%n".formatted (getTotalLooseObjects ()));

    if (getTotalPackFiles () > 0)
    {
      text.append ("Pack files .............. %,d%n".formatted (getTotalPackFiles ()));
      text.append (
          "Packed objects .......... %,d%n".formatted (getTotalPackedObjects ()));
    }

    text.append ("Branches ................ %,d%n".formatted (branches.size ()));

    for (Branch branch : branches)
    {
      String label = branch.name () + " .........................";
      text.append ("  %23.23s %6.6s%n".formatted (label, branch.sha ()));
    }

    if (remotes.size () > 0)
    {
      text.append ("Remotes ................. %,d%n".formatted (remotes.size ()));

      for (Remote remote : remotes)
      {
        String label = remote.name () + " .........................";
        if (remote.sha ().startsWith ("ref:"))
          text.append (
              "  %23.23s %s %s%n".formatted (label, remote.sha (), remote.head ()));
        else
          text.append (
              "  %23.23s %6.6s %s%n".formatted (label, remote.sha (), remote.head ()));
      }
    }

    text.append ("HEAD .................... %s%n".formatted (fullHead));

    if (!fetchHead.isEmpty ())
      text.append ("FETCH_HEAD .............. %s %s%n"
          .formatted (fetchHead.substring (0, 6), fetchHead.substring (42)));

    return Utility.rtrim (text);
  }
}
