package com.bytezone.gitbrowser;

// -----------------------------------------------------------------------------------//
public final class Blob extends GitObject
// -----------------------------------------------------------------------------------//
{
  String contents;

  // ---------------------------------------------------------------------------------//
  public Blob (String sha, byte[] data)
  // ---------------------------------------------------------------------------------//
  {
    super (sha, data, ObjectType.BLOB);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder (super.toString ());

    text.append ("%n%s%n".formatted (LINE));

    if (contents == null)
      contents = new String (buffer);

    text.append (contents);

    return text.toString ();
  }
}
