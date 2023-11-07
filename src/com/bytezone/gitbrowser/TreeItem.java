package com.bytezone.gitbrowser;

import java.io.UnsupportedEncodingException;

// -----------------------------------------------------------------------------------//
public class TreeItem
// -----------------------------------------------------------------------------------//
{
  final String sha1;
  final String permissions;
  final String name;
  final int length;

  // ---------------------------------------------------------------------------------//
  public TreeItem (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    int start = ptr;

    while (buffer[ptr] != 0)
      ptr++;

    String contents = getString (buffer, start, ptr - start);
    int pos = contents.indexOf (' ');

    permissions = contents.substring (0, pos);
    name = contents.substring (pos + 1);

    sha1 = Utility.getSha1 (buffer, ptr + 1);
    length = ptr - start + 21;
  }

  // ---------------------------------------------------------------------------------//
  private String getString (byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      return new String (buffer, offset, length, "UTF-8");
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace ();
      return "";
    }
  }

  // ---------------------------------------------------------------------------------//
  int getLength ()
  // ---------------------------------------------------------------------------------//
  {
    return length;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return "%-6.6s  %-6s  %s".formatted (sha1, permissions, name);
  }
}
