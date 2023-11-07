package com.bytezone.gitbrowser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

// ---------------------------------------------------------------------------------//
public class Action
// ---------------------------------------------------------------------------------//
{
  final String name;
  final String email;
  final LocalDateTime date;
  final DateTimeFormatter formatter = DateTimeFormatter.ofPattern ("yyyy-MM-dd HH:mm");

  // ---------------------------------------------------------------------------------//
  public Action (String text)
  // ---------------------------------------------------------------------------------//
  {
    String details = skipFirst (text);
    int pos1 = details.indexOf ('<');
    int pos2 = details.indexOf ('>');

    name = details.substring (0, pos1 - 1);
    email = details.substring (pos1 + 1, pos2);

    String dateText = details.substring (pos2 + 2).trim ();

    String[] chunks = dateText.split (" ");
    long seconds = Integer.parseInt (chunks[0]);
    long secondsAdjusted = Integer.parseInt (chunks[1]) / 100 * 60;

    Instant instant = Instant.ofEpochSecond (seconds + secondsAdjusted, 0);

    date = LocalDateTime.ofInstant (instant, ZoneOffset.UTC);
  }

  // ---------------------------------------------------------------------------------//
  protected String skipFirst (String line)
  // ---------------------------------------------------------------------------------//
  {
    return line.substring (line.indexOf (' ') + 1);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("%s  %s", name, date.format (formatter));
  }
}
