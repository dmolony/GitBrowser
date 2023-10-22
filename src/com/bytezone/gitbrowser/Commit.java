package com.bytezone.gitbrowser;

import java.util.List;

// -----------------------------------------------------------------------------------//
public final class Commit extends GitObject
// -----------------------------------------------------------------------------------//
{
  private List<String> commitLines;

  private int parentIndex;                // 0, or last parent line
  private int authorIndex;
  private int committerIndex;

  // ---------------------------------------------------------------------------------//
  public Commit (String name, byte[] data)
  // ---------------------------------------------------------------------------------//
  {
    super (name, data, ObjectType.COMMIT);

    commitLines = split (data);

    int lineNo = 0;
    for (String line : commitLines)
    {
      if (line.startsWith ("parent"))             // could be 0, 1 or 2 of these
        parentIndex = lineNo;
      else if (line.startsWith ("author"))
        authorIndex = lineNo;
      else if (line.startsWith ("committer"))
      {
        committerIndex = lineNo;
        break;                        // following lines are the commit message
      }

      lineNo++;
    }
  }

  // ---------------------------------------------------------------------------------//
  public List<String> commitLines ()
  // ---------------------------------------------------------------------------------//
  {
    return commitLines;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder (super.toString ());

    text.append ("%n%s%n".formatted (LINE));

    text.append (
        "Tree ....... %s%n".formatted (skipFirst (commitLines.get (0)).substring (0, 6)));
    if (parentIndex > 0)
      for (int i = 1; i <= parentIndex; i++)
        text.append ("Parent ..... %s%n"
            .formatted (skipFirst (commitLines.get (i)).substring (0, 6)));
    text.append (
        "Author ..... %s%n".formatted (skipFirst (commitLines.get (authorIndex))));
    text.append (
        "Committer .. %s%n".formatted (skipFirst (commitLines.get (committerIndex))));

    for (int i = committerIndex + 1; i < commitLines.size (); i++)
      text.append ("%s%n".formatted (commitLines.get (i)));

    return Utility.rtrim (text);
  }
}
