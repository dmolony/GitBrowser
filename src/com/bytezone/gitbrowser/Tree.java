package com.bytezone.gitbrowser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// -----------------------------------------------------------------------------------//
public final class Tree extends GitObject implements Iterable<TreeItem>
// -----------------------------------------------------------------------------------//
{
  private final List<TreeItem> treeItems = new ArrayList<> ();

  // ---------------------------------------------------------------------------------//
  public Tree (String name, byte[] data)
  // ---------------------------------------------------------------------------------//
  {
    super (name, data, ObjectType.TREE);

    int ptr = 0;
    while (ptr < data.length)
    {
      TreeItem treeItem = new TreeItem (data, ptr);
      treeItems.add (treeItem);
      ptr += treeItem.getLength ();
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder (super.toString ());

    text.append ("%n%s%n".formatted (LINE));

    for (TreeItem treeItem : treeItems)
      text.append ("%s%n".formatted (treeItem));

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  public String getText (List<String> shaList)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder (super.toString ());

    text.append ("%n%s%n".formatted (LINE));

    for (TreeItem treeItem : treeItems)
    {
      String update = shaList.contains (treeItem.sha) ? "" : "updated";
      text.append ("%-52s %s%n".formatted (treeItem, update));
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Iterator<TreeItem> iterator ()
  // ---------------------------------------------------------------------------------//
  {
    return treeItems.iterator ();
  }
}
