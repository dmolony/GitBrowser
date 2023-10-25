package com.bytezone.gitbrowser;

import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

// -----------------------------------------------------------------------------------//
public class PackFileItem
// -----------------------------------------------------------------------------------//
{
  static byte[] tempBuffer = new byte[65536];
  static String[] typesText =
      { "???", "Commit", "Tree", "Blob", "Tag", "???", "Ofs Dlt", "Ref Dlt" };

  private final Header header;
  private final int offset;                 // unique identifier for this item
  private int rawLength;

  private byte[] data;                      // unpacked data
  private String sha1;                      // sha1 value stored in the index
  private GitObject gitObject;              // create as required

  // delta information
  private Size srcSize;
  private Size dstSize;
  private long refDeltaOffset;
  private PackFileItem baseOffsetItem;
  private String refDeltaSha1;
  private GitObject baseRefObject;

  // ---------------------------------------------------------------------------------//
  public PackFileItem (byte[] buffer, int ptr) throws DataFormatException
  // ---------------------------------------------------------------------------------//
  {
    offset = ptr;

    header = getHeader (buffer, ptr);
    ptr += header.headerSize;

    if (header.type == 6)                     // OBJ_OFS_DELTA
    {
      Size baseOffsetSize = getBaseOffsetSize (buffer, ptr);
      ptr += baseOffsetSize.headerSize;
      refDeltaOffset = offset - baseOffsetSize.value;
    }
    else if (header.type == 7)                // OBJ_REF_DELTA
    {
      refDeltaSha1 = Utility.getSha1 (buffer, ptr);
      ptr += 20;
    }

    Inflater decompresser = new Inflater ();
    decompresser.setInput (buffer, ptr, buffer.length - ptr);
    int resultLength = decompresser.inflate (tempBuffer);
    assert header.value == resultLength;

    ptr += decompresser.getBytesRead ();
    rawLength = ptr - offset;

    decompresser.end ();

    data = new byte[resultLength];
    System.arraycopy (tempBuffer, 0, data, 0, resultLength);

    if (isTypeDelta ())
    {
      srcSize = getSize (data, 0);                        // size of source object
      dstSize = getSize (data, srcSize.headerSize);       // size of new object
    }
  }

  // ---------------------------------------------------------------------------------//
  GitObject getObject ()
  // ---------------------------------------------------------------------------------//
  {
    if (gitObject == null)
    {
      byte[] buffer = getBuffer ();
      gitObject = switch (getBaseType ())
      {
        case 1 -> new Commit (sha1, buffer);
        case 2 -> new Tree (sha1, buffer);
        case 3 -> new Blob (sha1, buffer);
        case 4 -> new Tag (sha1, buffer);
        default -> null;
      };
    }

    return gitObject;
  }

  // ---------------------------------------------------------------------------------//
  byte[] getBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    if (header.type == 6)
      return rebuildBuffer (baseOffsetItem.getBuffer ());     // recursion!

    if (header.type == 7)
      return rebuildBuffer (baseRefObject.buffer);

    return data;
  }

  // ---------------------------------------------------------------------------------//
  byte[] rebuildBuffer (byte[] baseBuffer)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = new byte[(int) dstSize.value];

    int ptr = 0;
    int insPtr = srcSize.headerSize + dstSize.headerSize;

    while (insPtr < data.length)
    {
      if ((data[insPtr] & 0x80) == 0)               // ADD instruction
      {
        int dataSize = data[insPtr++] & 0x7F;
        System.arraycopy (data, insPtr, buffer, ptr, dataSize);

        insPtr += dataSize;
        ptr += dataSize;
      }
      else                                          // COPY instruction
      {
        CopyInstruction copyInstruction = getCopyInstruction (data, insPtr);
        System.arraycopy (baseBuffer, copyInstruction.recordOffset, buffer, ptr,
            copyInstruction.recordSize);

        insPtr += copyInstruction.instructionSize;
        ptr += copyInstruction.recordSize;
      }
    }

    assert ptr == dstSize.value;

    return buffer;
  }

  // ---------------------------------------------------------------------------------//
  void setSha1 (String sha1)
  // ---------------------------------------------------------------------------------//
  {
    this.sha1 = sha1;
  }

  // ---------------------------------------------------------------------------------//
  String getSha1 ()
  // ---------------------------------------------------------------------------------//
  {
    return sha1;
  }

  // ---------------------------------------------------------------------------------//
  int getRawLength ()
  // ---------------------------------------------------------------------------------//
  {
    return rawLength;
  }

  // ---------------------------------------------------------------------------------//
  long getOffset ()
  // ---------------------------------------------------------------------------------//
  {
    return offset;
  }

  // ---------------------------------------------------------------------------------//
  int getType ()
  // ---------------------------------------------------------------------------------//
  {
    return header.type;
  }

  // ---------------------------------------------------------------------------------//
  int getBaseType ()
  // ---------------------------------------------------------------------------------//
  {
    return isTypeDelta () ? baseOffsetItem.getBaseType () : header.type;
  }

  // ---------------------------------------------------------------------------------//
  long getSize ()
  // ---------------------------------------------------------------------------------//
  {
    return header.value;
  }

  // ---------------------------------------------------------------------------------//
  long getSrcSize ()
  // ---------------------------------------------------------------------------------//
  {
    return srcSize.value;
  }

  // ---------------------------------------------------------------------------------//
  long getDstSize ()
  // ---------------------------------------------------------------------------------//
  {
    return isTypeDelta () ? dstSize.value : header.value;
  }

  // ---------------------------------------------------------------------------------//
  boolean isTypeObject ()
  // ---------------------------------------------------------------------------------//
  {
    return header.type >= 1 && header.type <= 4;
  }

  // ---------------------------------------------------------------------------------//
  boolean isTypeDelta ()
  // ---------------------------------------------------------------------------------//
  {
    return header.type == 6 || header.type == 7;
  }

  // ---------------------------------------------------------------------------------//
  long getRefOffset ()
  // ---------------------------------------------------------------------------------//
  {
    return refDeltaOffset;            // internal object referenced by OBJ_OFS_DELTA
  }

  // ---------------------------------------------------------------------------------//
  String getRefSha1 ()
  // ---------------------------------------------------------------------------------//
  {
    return refDeltaSha1;              // external object referenced by OBJ_REF_DELTA
  }

  // ---------------------------------------------------------------------------------//
  void setRefObject (PackFileItem basePackFileItem)
  // ---------------------------------------------------------------------------------//
  {
    assert header.type == 6 && this.baseOffsetItem == null;

    this.baseOffsetItem = basePackFileItem;
  }

  // ---------------------------------------------------------------------------------//
  void setRefObject (GitObject baseGitObject)
  // ---------------------------------------------------------------------------------//
  {
    assert header.type == 7 && this.baseRefObject == null;

    this.baseRefObject = baseGitObject;
  }

  // GIT: unpack_object_header_buffer()
  // ---------------------------------------------------------------------------------//
  Header getHeader (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    int ptr = offset;
    boolean msb = (buffer[ptr] & 0x80) != 0;
    int type = (buffer[ptr] & 0x70) >>> 4;                  // 3 bits
    long recordSize = buffer[ptr++] & 0x0F;                 // 4 bits
    int width = 4;

    while (msb)
    {
      msb = (buffer[ptr] & 0x80) != 0;
      long size = (buffer[ptr++] & 0x7F) << width;          // append on the left
      recordSize |= size;
      width += 7;
    }

    return new Header (type, offset, ptr - offset, recordSize);
  }

  // GIT: packfile.c get_delta_base()
  // ---------------------------------------------------------------------------------//
  Size getBaseOffsetSize (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    int ptr = offset;
    int c = buffer[ptr++] & 0xFF;
    long baseOffset = c & 0x7F;

    while ((c & 0x80) != 0)
    {
      c = buffer[ptr++] & 0xFF;
      baseOffset = ((baseOffset + 1) << 7) | (c & 0x7F);    // append on the right
    }

    return new Size (ptr - offset, baseOffset);
  }

  // GIT: delta.h get_delta_hdr_size()
  // ---------------------------------------------------------------------------------//
  Size getSize (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    int ptr = offset;
    int c = buffer[ptr++] & 0xFF;
    long recordSize = c & 0x7F;
    int width = 7;

    while ((c & 0x80) != 0)
    {
      c = buffer[ptr++] & 0xFF;
      recordSize |= (c & 0x7F) << width;                    // append on the left
      width += 7;
    }

    return new Size (ptr - offset, recordSize);
  }

  // ---------------------------------------------------------------------------------//
  CopyInstruction getCopyInstruction (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    int ptr = offset;
    int recordOffset = 0;
    int recordSize = 0;

    long value = 0;
    byte code = buffer[ptr++];

    for (int i = 0; i < 7; i++)
    {
      value >>>= 8;
      if ((code & 0x01) != 0)
        value |= ((long) buffer[ptr++] & 0xFF) << 48;

      code >>>= 1;
    }

    recordOffset = (int) (value & 0xFFFFFFFF);
    recordSize = (int) ((value & 0xFFFFFFFF00000000L) >>> 32);

    if (recordSize == 0)            // I have not tested this
      recordSize = 0x10000;

    return new CopyInstruction (ptr - offset, recordSize, recordOffset);
  }

  // ---------------------------------------------------------------------------------//
  record Header (int type, int offset, int headerSize, long value)
  // ---------------------------------------------------------------------------------//
  {
  };

  // ---------------------------------------------------------------------------------//
  record Size (int headerSize, long value)
  // ---------------------------------------------------------------------------------//
  {
  };

  // ---------------------------------------------------------------------------------//
  record CopyInstruction (int instructionSize, int recordSize, int recordOffset)
  // ---------------------------------------------------------------------------------//
  {
  };

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("%-6.6s  %-7s  %,8d", sha1, typesText[header.type],
        header.value);

    //    String deltaRefDetails = "";
    //
    //    if (header.type == 6)
    //      deltaRefDetails = String.format ("%,7d  %,7d  %,7d", refDeltaOffset, srcSize.value,
    //          dstSize.value);
    //    else if (header.type == 7)
    //      deltaRefDetails =
    //          String.format ("%7.7s  %,7d  %,7d", refDeltaSha1, srcSize.value, dstSize.value);
    //
    //    return String.format ("%-6.6s  %-7s  %,7d  %,7d  %s", sha1, typesText[header.type],
    //        header.offset, header.value, deltaRefDetails);
  }
}
