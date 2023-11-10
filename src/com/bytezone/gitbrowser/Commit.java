package com.bytezone.gitbrowser;

import java.util.ArrayList;
import java.util.List;

// -----------------------------------------------------------------------------------//
public final class Commit extends GitObject
// -----------------------------------------------------------------------------------//
{
  private List<String> commitLines;

  private String treeSha;
  private Action author;
  private Action committer;

  private List<String> parents = new ArrayList<> ();
  private List<String> message = new ArrayList<> ();

  // ---------------------------------------------------------------------------------//
  public Commit (String name, byte[] data)
  // ---------------------------------------------------------------------------------//
  {
    super (name, data, ObjectType.COMMIT);

    commitLines = split (data);
    treeSha = skipFirst (commitLines.get (0));
    boolean inMessage = false;

    for (String line : commitLines)
    {
      if (inMessage)
        message.add (line);
      else if (line.startsWith ("parent"))             // could be any number of these
        parents.add (skipFirst (line));
      else if (line.startsWith ("author"))
        author = new Action (line);
      else if (line.startsWith ("committer"))
      {
        committer = new Action (line);
        inMessage = true;                // everything after this is the commit message
      }
    }
  }

  // ---------------------------------------------------------------------------------//
  public List<String> commitLines ()
  // ---------------------------------------------------------------------------------//
  {
    return commitLines;
  }

  // ---------------------------------------------------------------------------------//
  public List<String> getParents ()
  // ---------------------------------------------------------------------------------//
  {
    return parents;
  }

  // ---------------------------------------------------------------------------------//
  public String getTreeSha ()
  // ---------------------------------------------------------------------------------//
  {
    return treeSha;
  }

  // ---------------------------------------------------------------------------------//
  public String getFirstMessageLine ()
  // ---------------------------------------------------------------------------------//
  {
    return message.get (1);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder (super.toString ());

    text.append ("%n%s%n".formatted (LINE));

    text.append ("Tree ....... %6.6s%n".formatted (treeSha));

    for (String parent : parents)
      text.append ("Parent ..... %6.6s%n".formatted (parent));

    text.append ("Author ..... %s%n".formatted (author));
    text.append ("Committer .. %s%n".formatted (committer));

    for (String messageLine : message)
      text.append ("%s%n".formatted (messageLine));

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("%6.6s  %s  %s", sha, committer.getFormattedDate (),
        getFirstMessageLine ());
  }
}
