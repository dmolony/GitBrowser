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

  private List<String> parentShas = new ArrayList<> ();
  private List<String> message = new ArrayList<> ();

  // ---------------------------------------------------------------------------------//
  public Commit (String sha, byte[] data)
  // ---------------------------------------------------------------------------------//
  {
    super (sha, data, ObjectType.COMMIT);

    commitLines = split (data);
    treeSha = skipFirst (commitLines.get (0));          // it's always here
    boolean inMessage = false;

    for (String line : commitLines)
      if (inMessage)
        message.add (line);
      else if (line.startsWith ("parent"))             // could be any number of these
        parentShas.add (skipFirst (line));
      else if (line.startsWith ("author"))
        author = new Action (line);
      else if (line.startsWith ("committer"))
      {
        committer = new Action (line);
        inMessage = true;                // everything after this is the commit message
      }
  }

  // ---------------------------------------------------------------------------------//
  public List<String> getCommitLines ()
  // ---------------------------------------------------------------------------------//
  {
    return commitLines;
  }

  // ---------------------------------------------------------------------------------//
  public List<String> getParentShas ()
  // ---------------------------------------------------------------------------------//
  {
    return parentShas;
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
    for (String line : message)
      if (line.length () > 0)
        return line;

    return "< no message text >";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder (super.toString ());

    text.append ("%n%s%n".formatted (LINE));
    text.append ("Tree ....... %6.6s%n".formatted (treeSha));

    for (String parent : parentShas)
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
