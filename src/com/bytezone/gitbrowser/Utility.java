package com.bytezone.gitbrowser;

public class Utility
{

  // ---------------------------------------------------------------------------------//
  public static long unsignedInt (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      return (buffer[ptr] & 0xFF)             //
          | (buffer[ptr + 1] & 0xFF) << 8     //
          | (buffer[ptr + 2] & 0xFF) << 16    //
          | (buffer[ptr + 3] & 0xFF) << 24;
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      System.out.printf ("Index out of range (unsignedInt): %d > %d%n", ptr,
          buffer.length);
      return 0;
    }
  }

  // ---------------------------------------------------------------------------------//
  public static long unsignedLong (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      return (buffer[ptr] & 0xFF)             //
          | (buffer[ptr + 1] & 0xFF) << 8     //
          | (buffer[ptr + 2] & 0xFF) << 16    //
          | (buffer[ptr + 3] & 0xFF) << 24    //
          | (buffer[ptr + 3] & 0xFF) << 32    //
          | (buffer[ptr + 3] & 0xFF) << 40    //
          | (buffer[ptr + 3] & 0xFF) << 48    //
          | (buffer[ptr + 3] & 0xFF) << 56;
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      System.out.printf ("Index out of range (unsignedLong): %d > %d%n", ptr,
          buffer.length);
      return 0;
    }
  }

  // ---------------------------------------------------------------------------------//
  public static long unsignedIntBigEndian (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    int value = 0;
    for (int i = 0; i < 4; i++)
    {
      value <<= 8;
      value |= buffer[ptr++] & 0xFF;
    }

    return value;
  }

  // ---------------------------------------------------------------------------------//
  public static long unsignedLongBigEndian (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    long value = 0;
    for (int i = 0; i < 8; i++)
    {
      value <<= 8;
      value |= buffer[ptr++] & 0xFF;
    }

    return value;
  }

  // ---------------------------------------------------------------------------------//
  public static String getSha1 (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (int i = 0; i < 20; i++)
      text.append (String.format ("%02x", buffer[offset++]));

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public static String rtrim (StringBuilder text)
  // ---------------------------------------------------------------------------------//
  {
    if (text.length () > 0)
      while (text.charAt (text.length () - 1) == '\n')
        text.deleteCharAt (text.length () - 1);

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public static void dump (byte[] data)
  // ---------------------------------------------------------------------------------//
  {
    dump (data, 0, data.length);
  }

  // ---------------------------------------------------------------------------------//
  public static void dump (byte[] data, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    int ptr = offset;
    int max = Math.min (offset + length, data.length);

    StringBuilder text1 = new StringBuilder ();
    StringBuilder text2 = new StringBuilder ();

    while (ptr < max)
    {
      int lineSize = Math.min (max - ptr, 16);

      text1.setLength (0);
      text2.setLength (0);

      for (int i = 0; i < lineSize; i++)
      {
        int b = data[ptr + i] & 0xFF;
        text1.append (String.format ("%02X ", b));

        if (b < 32 || b == 0xFF)
          text2.append (".");
        else
          text2.append ((char) b);
      }

      System.out.printf ("%-48s %s%n", text1.toString (), text2.toString ());

      ptr += 16;
    }
  }
}
