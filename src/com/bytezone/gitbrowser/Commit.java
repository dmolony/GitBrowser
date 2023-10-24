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

  private String treeSha;

  // ---------------------------------------------------------------------------------//
  public Commit (String name, byte[] data)
  // ---------------------------------------------------------------------------------//
  {
    super (name, data, ObjectType.COMMIT);

    commitLines = split (data);
    treeSha = skipFirst (commitLines.get (0)).toUpperCase ();

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
  public String getTreeSha ()
  // ---------------------------------------------------------------------------------//
  {
    return treeSha;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder (super.toString ());

    text.append ("%n%s%n".formatted (LINE));

    text.append ("Tree ....... %6.6s%n".formatted (treeSha));
    if (parentIndex > 0)
      for (int i = 1; i <= parentIndex; i++)
        text.append ("Parent ..... %6.6s%n".formatted (skipFirst (commitLines.get (i))));
    text.append (
        "Author ..... %s%n".formatted (skipFirst (commitLines.get (authorIndex))));
    text.append (
        "Committer .. %s%n".formatted (skipFirst (commitLines.get (committerIndex))));

    for (int i = committerIndex + 1; i < commitLines.size (); i++)
      text.append ("%s%n".formatted (commitLines.get (i)));

    return Utility.rtrim (text);
  }
}
