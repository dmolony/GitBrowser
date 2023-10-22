package com.bytezone.gitbrowser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.DataFormatException;

// -----------------------------------------------------------------------------------//
public class PackFile implements Iterable<PackFileItem>
// -----------------------------------------------------------------------------------//
{
  static byte[] buffer = new byte[65536];
  static String[] typesText =
      { "???", "Commit", "Tree", "Blob", "Tag", "???", "Ofs Dlt", "Ref Dlt" };

  File indexFile;
  File packFile;
  File revFile;

  List<PackFileItem> packFileItems = new ArrayList<> ();
  List<IndexFileItem> indexFileItems = new ArrayList<> ();

  Map<Long, PackFileItem> offsetListPackFile = new TreeMap<> ();
  Map<Long, IndexFileItem> offsetList = new TreeMap<> ();

  String packFileSha1;
  int totFiles;

  String[] sha1;
  long[] offsetsIndex;
  long[] crc;

  final Map<String, GitObject> objectsBySha;

  // ---------------------------------------------------------------------------------//
  public PackFile (File file, Map<String, GitObject> objectsBySha)       // index file
  // ---------------------------------------------------------------------------------//
  {
    packFileSha1 = getSha1 (file);
    this.objectsBySha = objectsBySha;

    long[] fanout = new long[256];
    this.indexFile = file;

    try
    {
      byte[] content = Files.readAllBytes (file.toPath ());
      String toc = new String (content, 1, 3);
      long version = Utility.unsignedIntBigEndian (content, 4);

      assert content[0] == (byte) 0xFF;
      assert toc.equals ("tOc");
      assert version == 2;

      for (int i = 0; i < 256; i++)
        fanout[i] = Utility.unsignedIntBigEndian (content, 8 + i * 4);

      totFiles = (int) fanout[0xFF];

      sha1 = new String[totFiles];
      offsetsIndex = new long[totFiles];
      crc = new long[totFiles];

      int ptr = 0x408;                        // 1032
      for (int i = 0; i < totFiles; i++)
      {
        sha1[i] = Utility.getSha1 (content, ptr);
        ptr += 20;
      }

      for (int i = 0; i < totFiles; i++)
      {
        crc[i] = Utility.unsignedInt (content, ptr);
        ptr += 4;
      }

      int largeOffsetPtr = 1032 + totFiles * 28;
      for (int i = 0; i < totFiles; i++)
      {
        boolean largeOffset = (content[ptr] & 0x80) != 0;
        offsetsIndex[i] = Utility.unsignedIntBigEndian (content, ptr);
        //        if (largeOffset)
        //        {
        //          long ofs = offset[i] & 0x7FFFFFFF;
        //  offsetLong[i] = Utility.unsignedLongBigEndian (content, largeOffsetPtr + ofs);
        //        }

        ptr += 4;
      }

      if (false)
      {
        String packFileSha1 = Utility.getSha1 (content, ptr);
        String indexFileSha1 = Utility.getSha1 (content, ptr + 20);
        System.out.println (packFileSha1);
        System.out.println (indexFileSha1);
      }

      assert ptr + 40 == content.length;

      for (int i = 0; i < totFiles; i++)
      {
        IndexFileItem indexFileItem =
            new IndexFileItem (sha1[i], crc[i], offsetsIndex[i]);
        indexFileItems.add (indexFileItem);
        offsetList.put (indexFileItem.offset, indexFileItem);
      }

      if (false)
      {
        for (IndexFileItem indexFileItem : indexFileItems)
          System.out.println (indexFileItem);

        System.out.println ();

        for (IndexFileItem indexFileItem : offsetList.values ())
          System.out.println (indexFileItem);
      }

    }
    catch (IOException e)
    {
      e.printStackTrace ();
    }
  }

  // ---------------------------------------------------------------------------------//
  void addPack (File file)
  // ---------------------------------------------------------------------------------//
  {
    //    System.out.printf ("%nPack File : %s%n%n", file.getName ());
    assert packFileSha1.equals (getSha1 (file));

    try
    {
      byte[] content = Files.readAllBytes (file.toPath ());

      String pack = new String (content, 0, 4);
      assert pack.equals ("PACK");

      long version = Utility.unsignedIntBigEndian (content, 4);
      assert version == 2 || version == 3;

      long totalPackFiles = Utility.unsignedIntBigEndian (content, 8);
      assert totFiles == totalPackFiles;

      int ptr = 12;

      for (int i = 0; i < totFiles; i++)
      {
        PackFileItem packFileItem = new PackFileItem (content, ptr);
        packFileItems.add (packFileItem);
        offsetListPackFile.put ((long) ptr, packFileItem);
        ptr += packFileItem.getLength ();

        packFileItem.setSha1 (offsetList.get (packFileItem.getOffset ()).sha1);

        if (packFileItem.getType () == 6)
        {
          PackFileItem basePackFileItem =
              offsetListPackFile.get (packFileItem.getBaseOffset ());

          assert basePackFileItem != null;
          assert packFileItem.getSrcSize () == basePackFileItem.getDstSize ();

          packFileItem.setBase (basePackFileItem);
        }
        else if (packFileItem.getType () == 7)
        {
          String baseSha1 = packFileItem.getRefSha1 ();
          packFileItem.setBase (objectsBySha.get (baseSha1));
        }
      }

      assert ptr + 20 == content.length;
    }
    catch (IOException | DataFormatException e)
    {
      e.printStackTrace ();
    }
  }

  // ---------------------------------------------------------------------------------//
  void addReverse (File file)
  // ---------------------------------------------------------------------------------//
  {
    assert shaMatches (file);

    long[] reverse = new long[totFiles];          // totFiles

    try
    {
      byte[] content = Files.readAllBytes (file.toPath ());
      String head = new String (content, 0, 4);
      assert head.equals ("RIDX");

      long revVersion = Utility.unsignedIntBigEndian (content, 4);    // always 1
      long hashVersion = Utility.unsignedIntBigEndian (content, 8);   // 1=sha1, 2=sha256

      assert revVersion == 1;
      assert hashVersion == 1;

      int ptr = 12;
      for (int i = 0; i < totFiles; i++)
      {
        long value = Utility.unsignedIntBigEndian (content, ptr);
        reverse[i] = value;
        ptr += 4;
      }

      assert ptr + 40 == content.length;

      if (false)
      {
        String packFileSha1 = Utility.getSha1 (content, ptr);
        String revFileSha1 = Utility.getSha1 (content, ptr + 20);
        System.out.println (packFileSha1);
        System.out.println (revFileSha1);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace ();
    }
  }

  // ---------------------------------------------------------------------------------//
  private String getSha1 (File file)
  // ---------------------------------------------------------------------------------//
  {
    String fileName = file.getName ();

    int pos1 = fileName.indexOf ('-');
    int pos2 = fileName.lastIndexOf ('.');

    return fileName.substring (pos1 + 1, pos2);
  }

  // ---------------------------------------------------------------------------------//
  boolean shaMatches (File file)
  // ---------------------------------------------------------------------------------//
  {
    return packFileSha1.equals (getSha1 (file));
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Iterator<PackFileItem> iterator ()
  // ---------------------------------------------------------------------------------//
  {
    return packFileItems.iterator ();
  }
}
