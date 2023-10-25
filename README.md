# GitBrowser

## Example Output
```
Project .... GitBrowser

Objects ....     196
Commits ....      56
Trees ......      78
Blobs ......      62
Tags .......       0
Packfiles ..       1

Ndx  SHA-1   Type      Length
---  ------  ------  --------
  0  618cbd  COMMIT       246  
  1  0d0ccd  TREE          30  
  2  0dff4e  TREE          67  
  3  952f3a  TREE          67  
  4  9205b5  COMMIT       255  
...
...
...
181  71f965  BLOB       1,294  README.md
182  76597c  BLOB       3,810  README.md
183  76fe7d  BLOB       2,820  GitObject.java
184  1cfc45  BLOB       6,271  GitBrowser.java
185  1ccd24  COMMIT       246  
186  49d0ba  TREE          67  
187  49d306  BLOB         252  README.md
188  406bd3  COMMIT       246  
189  2b8934  BLOB         196  README.md
190  47900e  TREE          35  
191  78a9e9  TREE         496  
192  786e95  COMMIT       251  
193  7fb9c5  TREE          67  
194  7ffeef  COMMIT       246  
195  25f33d  TREE          67  

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
  0  B7981C  Commit        12      209  
  1  C39B23  Tree         149       30  
  2  6DD3DA  Tree         188       30  
  3  E7A84F  Tree         228       35  
  4  AB9288  Tree         272       37  
  5  FE4D35  Tree         318      496  
  6  07B8DF  Blob         703    2,447  
  7  295CEC  Ofs Dlt    1,380      193      703    2,447    1,004
  8  C7BDA9  Blob       1,534    6,230  
  9  FA665C  Ofs Dlt    2,909    1,173      703    2,447    2,818
 10  A05498  Blob       3,498    2,012  
 11  14120B  Ofs Dlt    4,182      401    2,909    2,818    1,242
 12  E7E8CC  Blob       4,434    7,714  
 13  9BC8C0  Blob       6,149   11,967  
 14  EF16DE  Ofs Dlt    8,178      513      703    2,447    1,666
 15  F7B759  Ofs Dlt    8,470      514      703    2,447    1,600
 16  D412D6  Ofs Dlt    8,798      676    6,149   11,967    1,546
 17  B6A3DE  Blob       9,207    4,629  
 ```
## Rebuilding a packed delta file
 Object 14120B refers to FA665C which refers to 07B8DF.
 ```
14120B  BLOB       1,242
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
