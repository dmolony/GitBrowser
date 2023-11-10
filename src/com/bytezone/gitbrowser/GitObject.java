package com.bytezone.gitbrowser;

import java.util.ArrayList;
import java.util.List;

// -----------------------------------------------------------------------------------//
public abstract sealed class GitObject permits Commit, Tree, Blob, Tag
// -----------------------------------------------------------------------------------//
{
  static final String LINE =
      "------------------------------------------------------------";

  protected final String sha;
  protected final byte[] buffer;
  protected final ObjectType objectType;

  enum ObjectType
  {
    COMMIT, TREE, BLOB, TAG
  }

  // ---------------------------------------------------------------------------------//
  public GitObject (String sha, byte[] buffer, ObjectType objectType)
  // ---------------------------------------------------------------------------------//
  {
    this.sha = sha;
    this.buffer = buffer;
    this.objectType = objectType;
  }

  // ---------------------------------------------------------------------------------//
  public abstract String getText ();
  // ---------------------------------------------------------------------------------//

  // ---------------------------------------------------------------------------------//
  String getSha ()
  // ---------------------------------------------------------------------------------//
  {
    return sha;
  }

  // ---------------------------------------------------------------------------------//
  ObjectType getObjectType ()
  // ---------------------------------------------------------------------------------//
  {
    return objectType;
  }

  // ---------------------------------------------------------------------------------//
  List<String> split (byte[] data)
  // ---------------------------------------------------------------------------------//
  {
    List<String> lines = new ArrayList<> ();

    int ptr = 0;
    int start = 0;

    while (ptr < data.length)
      if (data[ptr++] == (byte) 0x0A)
      {
        lines.add (new String (data, start, ptr - start - 1));
        start = ptr;
      }

    if (start < data.length)
      lines.add (new String (data, start, data.length - start));

    return lines;
  }

  // ---------------------------------------------------------------------------------//
  protected String skipFirst (String line)
  // ---------------------------------------------------------------------------------//
  {
    return line.substring (line.indexOf (' ') + 1);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("%-6.6s  %-6s  %,8d", sha, objectType, buffer.length);
  }
}
