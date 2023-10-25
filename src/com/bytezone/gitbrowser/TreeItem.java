package com.bytezone.gitbrowser;

import java.io.UnsupportedEncodingException;

// -----------------------------------------------------------------------------------//
public class TreeItem
// -----------------------------------------------------------------------------------//
{
  String sha1;
  String permissions;
  String name;
  int length;

  // ---------------------------------------------------------------------------------//
  public TreeItem (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    int start = ptr;

    while (buffer[ptr++] != 0)
      ;

    try
    {
      String contents = new String (buffer, start, ptr - start, "UTF-8");
      int pos = contents.indexOf (' ');
      permissions = contents.substring (0, pos);
      name = contents.substring (pos + 1).trim ();
      sha1 = Utility.getSha1 (buffer, ptr);
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace ();
    }

    length = ptr + 20 - start;
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
    return "%-6.6s  %-7s  %s".formatted (sha1, permissions, name);
  }
}
