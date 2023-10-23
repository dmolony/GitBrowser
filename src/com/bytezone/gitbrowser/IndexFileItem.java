package com.bytezone.gitbrowser;

// -----------------------------------------------------------------------------------//
public class IndexFileItem implements Comparable<IndexFileItem>
// -----------------------------------------------------------------------------------//
{
  final String sha1;
  final long crc;
  final long offset;

  // ---------------------------------------------------------------------------------//
  public IndexFileItem (String sha1, long crc, long offset)
  // ---------------------------------------------------------------------------------//
  {
    this.sha1 = sha1;
    this.crc = crc;
    this.offset = offset;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int compareTo (IndexFileItem o)
  // ---------------------------------------------------------------------------------//
  {
    return this.offset == o.offset ? 0 : this.offset < o.offset ? -1 : 1;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("%,7d  %s %,15d", offset, sha1, crc);
  }
}
