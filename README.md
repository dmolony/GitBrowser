# GitBrowser
Just some experimental code to examine the files in the .git/objects folder. 
The documentation on the web is not entirely accurate.
## Example Output
```
Project .... GitBrowser

Objects ....      83
Commits ....      17
Trees ......      41
Blobs ......      25
Tags .......       0
Packfiles ..       1

Ndx  SHA-1   Type      Length
---  ------  ------  --------
  0  3ea331  COMMIT       252  
  1  57e052  COMMIT       249  
  2  6f2367  TREE          30  
  3  9e71e5  TREE          30  
  4  9e9cc3  TREE          37  
  5  32b187  TREE          37  
  6  51221a  BLOB       2,379  README.md 
  7  93300a  TREE          67  
  8  5fe6ec  COMMIT       266  
  9  d9922b  TREE          35  
 10  ad1856  TREE          37  
 11  be7099  BLOB       7,309  GitBrowser.java 
 12  be6e99  COMMIT       250  
 13  a239c7  BLOB       2,138  README.md 
 14  bdb293  TREE          67  
 15  ae2ad5  TREE          67  
 16  e550a9  COMMIT       249  
 17  e2573c  TREE          67  
 18  c78fb1  COMMIT       253  
 19  eea29b  BLOB      12,543  PackFileItem.java 
 20  f2256a  BLOB       6,325  GitBrowser.java 
 21  f5c924  BLOB       1,260  IndexFileItem.java 
 22  116c25  TREE          35  
 23  7cb6f5  TREE          35  
 24  8926d1  TREE          67  
 25  45607d  TREE          30  
 26  45f028  TREE         496  
 27  737dc9  TREE          67  
 28  87b722  COMMIT       268  
 29  8a29ab  TREE          67  
 30  21f315  BLOB       4,987  README.md 
 31  212995  BLOB       2,699  Commit.java 
 32  816937  BLOB       2,408  README.md 
 33  9f2917  TREE          35  
 34  9fccb0  COMMIT       278  
 35  07f403  COMMIT       261  
 36  00b59e  TREE          30  
 37  6ed9ac  BLOB       2,380  README.md 
 38  9a2262  TREE          35  
 39  9a44a2  BLOB       2,323  README.md 
 40  360d35  BLOB          89  README.md 
 41  5d314f  TREE         496  
 42  96d1bf  TREE          37  
 43  549979  TREE          30  
 44  98e8f7  COMMIT       263  
 45  53504d  TREE          37  
 46  301a2e  BLOB       8,162  PackFile.java 
 47  08cb83  TREE          67  
 48  6dbb03  COMMIT       252  
 49  397f66  TREE         496  
 50  99681f  BLOB       2,147  README.md 
 51  9719e8  COMMIT       267  
 52  640862  TREE          67  
 53  908198  TREE          37  
 54  d4bf0f  TREE          67  
 55  a0f767  BLOB       6,224  GitBrowser.java 
 56  dc10ff  TREE          67  
 57  ef3518  TREE          67  
 58  c324d7  TREE          30  
 59  cce829  TREE         496  
 60  f90b9f  BLOB       6,228  GitBrowser.java 
 61  fa0a59  COMMIT       258  
 62  ffe61e  BLOB       7,968  GitBrowser.java 
 63  c54984  COMMIT       254  
 64  f13bd0  TREE         496  
 65  f1ab15  COMMIT       265  
 66  f8ae9e  BLOB       3,614  README.md 
 67  466bdb  COMMIT       260  
 68  79033f  TREE          30  
 69  48277b  BLOB       8,243  PackFile.java 
 70  70d324  TREE          67  
 71  1edbc7  BLOB       2,152  README.md 
 72  1e3aa9  TREE          67  
 73  230758  BLOB       3,745  README.md 
 74  4f338b  BLOB       4,989  README.md 
 75  8554ee  TREE          30  
 76  76597c  BLOB       3,810  README.md 
 77  76fe7d  BLOB       2,820  GitObject.java 
 78  1cfc45  BLOB       6,271  GitBrowser.java 
 79  47900e  TREE          35  
 80  78a9e9  TREE         496  
 81  786e95  COMMIT       251  
 82  25f33d  TREE          67

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
