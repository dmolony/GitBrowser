package com.bytezone.gitbrowser;

// -----------------------------------------------------------------------------------//
public class IndexFileItem implements Comparable<IndexFileItem>
// -----------------------------------------------------------------------------------//
{
  final String sha;
  final long crc;
  final long offset;

  // ---------------------------------------------------------------------------------//
  public IndexFileItem (String sha, long crc, long offset)
  // ---------------------------------------------------------------------------------//
  {
    this.sha = sha;
    this.crc = crc;
    this.offset = offset;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int compareTo (IndexFileItem o)
  // ---------------------------------------------------------------------------------//
  {
    return this.offset < o.offset ? -1 : this.offset > o.offset ? 1 : 0;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("%,7d  %s %,15d", offset, sha, crc);
  }
}
