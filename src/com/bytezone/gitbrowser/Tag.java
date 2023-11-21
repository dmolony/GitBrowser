package com.bytezone.gitbrowser;

import java.util.List;

// -----------------------------------------------------------------------------------//
public final class Tag extends GitObject
// -----------------------------------------------------------------------------------//
{
  private List<String> tagLines;
  private String objectHash;
  private String type;
  private String tag;
  private String tagger;

  // ---------------------------------------------------------------------------------//
  public Tag (String sha, byte[] data)
  // ---------------------------------------------------------------------------------//
  {
    super (sha, data, ObjectType.TAG);

    tagLines = split (data);

    for (String line : tagLines)
    {
      if (line.startsWith ("object"))
        objectHash = skipFirst (line);
      else if (line.startsWith ("type"))
        type = skipFirst (line);
      if (line.startsWith ("tag"))
        tag = skipFirst (line);
      if (line.startsWith ("tagger"))
        tagger = skipFirst (line);
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder (super.toString ());

    text.append ("%n%s%n".formatted (LINE));

    text.append ("Object ..... %s%n".formatted (objectHash.substring (0, 6)));
    text.append ("Type ....... %s%n".formatted (type));
    text.append ("Tag ........ %s%n".formatted (tag));
    text.append ("Tagger ..... %s%n".formatted (tagger));

    return Utility.rtrim (text);
  }
}
