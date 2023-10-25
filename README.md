# GitBrowser

## Example Output
```
Project .... GitBrowser

Objects ....     210
Commits ....      59
Trees ......      85
Blobs ......      66
Tags .......       0
Packfiles ..       1

Ndx  SHA-1   Type      Length
---  ------  ------  --------
  0  00b59e  TREE          30  
  1  010657  BLOB       3,334  README.md
  2  05c8cd  COMMIT       246  
  3  074397  COMMIT       246  
  4  07f403  COMMIT       261  
  5  08cb83  TREE          67  
  6  08f2c9  BLOB       8,245  PackFile.java
 ...
 ...
 ...
200  f55a8e  COMMIT       246  
201  f5c924  BLOB       1,260  IndexFileItem.java
202  f66819  TREE          67  
203  f8ae9e  BLOB       3,614  README.md
204  f90b9f  BLOB       6,228  GitBrowser.java
205  f9a33a  BLOB         513  README.md
206  fa0a59  COMMIT       258  
207  fb1158  BLOB         195  README.md
208  fd42fd  BLOB         201  README.md
209  ffe61e  BLOB       7,968  GitBrowser.java  

Pack: a5c6628afc0b0a3981e47a2ced178e64ba19cb93

Objects ....      18
Commits ....       1
Trees ......       5
Blobs ......       6
Tags .......       0
Delta ofs ..       6
Delta ref ..       0

Ndx  SHA-1   Type      Offset  DstSize  RefOfst  SrcSize  DstSize
---  ------  -------  -------  -------  -------  -------  -------
  0  b7981c  Commit        12      209  
  1  c39b23  Tree         149       30  
  2  6dd3da  Tree         188       30  
  3  e7a84f  Tree         228       35  
  4  ab9288  Tree         272       37  
  5  fe4d35  Tree         318      496  
  6  07b8df  Blob         703    2,447  
  7  295cec  Ofs Dlt    1,380      193      703    2,447    1,004
  8  c7bda9  Blob       1,534    6,230  
  9  fa665c  Ofs Dlt    2,909    1,173      703    2,447    2,818
 10  a05498  Blob       3,498    2,012  
 11  14120b  Ofs Dlt    4,182      401    2,909    2,818    1,242
 12  e7e8cc  Blob       4,434    7,714  
 13  9bc8c0  Blob       6,149   11,967  
 14  ef16de  Ofs Dlt    8,178      513      703    2,447    1,666
 15  f7b759  Ofs Dlt    8,470      514      703    2,447    1,600
 16  d412d6  Ofs Dlt    8,798      676    6,149   11,967    1,546
 17  b6a3de  Blob       9,207    4,629    
 ```
## Rebuilding a packed delta file
 Object 14120b refers to Fa665c which refers to 07b8df.
 ```
14120b  BLOB       1,242
------------------------------------------------------------
package com.bytezone.gitbrowser;

// -----------------------------------------------------------------------------------//
public class IndexFileItem implements Comparable<IndexFileItem>
// -----------------------------------------------------------------------------------//
{
  String sha1;
  long crc;
  long offset;

  // ---------------------------------------------------------------------------------//
  public IndexFileItem (String sha1, long crc, long offset)
  // ---------------------------------------------------------------------------------//
  {
    this.sha1 = sha1;
    this.crc = crc;
    this.offset = offset;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int compareTo (IndexFileItem o)
  // ---------------------------------------------------------------------------------//
  {
    return this.offset == o.offset ? 0 : this.offset < o.offset ? -1 : 1;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("%,7d  %s %,15d", offset, sha1, crc);
  }
}
```
