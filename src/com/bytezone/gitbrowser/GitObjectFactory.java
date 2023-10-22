package com.bytezone.gitbrowser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

// https://www.alibabacloud.com/blog/597391
// -----------------------------------------------------------------------------------//
public class GitObjectFactory
// -----------------------------------------------------------------------------------//
{
  static byte[] buffer = new byte[65536];

  // ---------------------------------------------------------------------------------//
  private GitObjectFactory ()
  // ---------------------------------------------------------------------------------//
  {
    // cannot instantiate
  }

  // ---------------------------------------------------------------------------------//
  public static GitObject getObject (File parent, File file)
  // ---------------------------------------------------------------------------------//
  {
    String name = parent.getName () + file.getName ();

    try
    {
      byte[] content = Files.readAllBytes (file.toPath ());

      Inflater decompresser = new Inflater ();
      decompresser.setInput (content, 0, content.length);
      int resultLength = decompresser.inflate (buffer);
      decompresser.end ();

      int ptr = 0;
      while (buffer[ptr++] != 0)
        ;

      String type = new String (buffer, 0, ptr - 1, "UTF-8");
      String[] chunks = type.split (" ");
      int dataLength = Integer.parseInt (chunks[1]);

      byte[] data = new byte[dataLength];
      System.arraycopy (buffer, resultLength - dataLength, data, 0, dataLength);

      return switch (chunks[0])
      {
        case "blob" -> new Blob (name, data);
        case "tree" -> new Tree (name, data);
        case "commit" -> new Commit (name, data);
        case "tag" -> new Tag (name, data);
        default -> null;
      };
    }
    catch (IOException | DataFormatException e)
    {
      e.printStackTrace ();
      return null;
    }
  }
}
