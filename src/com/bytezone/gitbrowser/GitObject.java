package com.bytezone.gitbrowser;

import java.util.ArrayList;
import java.util.List;

// -----------------------------------------------------------------------------------//
public abstract sealed class GitObject permits Commit, Tree, Blob, Tag
// -----------------------------------------------------------------------------------//
{
  static final String LINE =
      "------------------------------------------------------------";

  protected final String name;
  protected final byte[] data;
  protected final ObjectType objectType;

  enum ObjectType
  {
    COMMIT, TREE, BLOB, TAG
  }

  // ---------------------------------------------------------------------------------//
  public GitObject (String name, byte[] data, ObjectType objectType)
  // ---------------------------------------------------------------------------------//
  {
    this.name = name;                     // sha string
    this.data = data;
    this.objectType = objectType;
  }

  // ---------------------------------------------------------------------------------//
  public abstract String getText ();
  // ---------------------------------------------------------------------------------//

  // ---------------------------------------------------------------------------------//
  String getSha ()
  // ---------------------------------------------------------------------------------//
  {
    return name;
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
    return String.format ("%-6s  %,8d  %s  ", objectType, data.length, name);
  }
}
