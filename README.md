# GitBrowser

## Example Output
```
Project .... GitBrowser

Objects ....     224
Commits ....      61
Trees ......      91
Blobs ......      72
Tags .......       0
Packfiles ..       1

Ndx  SHA-1   Type      Length  Name
---  ------  ------  --------  ----------------------------
  0  00b59e  TREE          30  
  1  010657  BLOB       3,334  README.md
  2  05c8cd  COMMIT       246  
  3  074397  COMMIT       246  
  4  07f403  COMMIT       261  
  5  0865bd  BLOB       3,566  README.md
  6  08cb83  TREE          67  
  7  08f2c9  BLOB       8,245  PackFile.java
  8  09e196  BLOB       1,888  TreeItem.java
  9  0c56fd  BLOB         204  README.md
 10  0d0ccd  TREE          30  src
...
...
...
211  f2256a  BLOB       6,325  GitBrowser.java
212  f466f9  COMMIT       246  
213  f4e72e  BLOB       6,005  README.md
214  f55a8e  COMMIT       246  
215  f5c924  BLOB       1,260  IndexFileItem.java
216  f66819  TREE          67  
217  f8ae9e  BLOB       3,614  README.md
218  f90b9f  BLOB       6,228  GitBrowser.java
219  f9a33a  BLOB         513  README.md
220  fa0a59  COMMIT       258  
221  fb1158  BLOB         195  README.md
222  fd42fd  BLOB         201  README.md
223  ffe61e  BLOB       7,968  GitBrowser.java

Pack: a5c6628afc0b0a3981e47a2ced178e64ba19cb93

Objects ....      18
Commits ....       1
Trees ......       5
Blobs ......       6
Tags .......       0
Delta ofs ..       6
Delta ref ..       0

Ndx  SHA-1   Type     Link      Length  Name
---  ------  -------  ------  --------  -------------------------
  0  07b8df  Blob                2,447  Commit.java
  1  14120b  Ofs Dlt  fa665c       401  IndexFileItem.java
  2  295cec  Ofs Dlt  07b8df       193  Blob.java
  3  6dd3da  Tree                   30  src
  4  9bc8c0  Blob               11,967  PackFileItem.java
  5  a05498  Blob                2,012  GitObjectFactory.java
  6  ab9288  Tree                   37  bytezone
  7  b6a3de  Blob                4,629  Utility.java
  8  b7981c  Commit   c39b23       209  
  9  c39b23  Tree                   30  
 10  c7bda9  Blob                6,230  GitBrowser.java
 11  d412d6  Ofs Dlt  9bc8c0       676  TreeItem.java
 12  e7a84f  Tree                   35  com
 13  e7e8cc  Blob                7,714  PackFile.java
 14  ef16de  Ofs Dlt  07b8df       513  Tag.java
 15  f7b759  Ofs Dlt  07b8df       514  Tree.java
 16  fa665c  Ofs Dlt  07b8df     1,173  GitObject.java
 17  fe4d35  Tree                  496  gitbrowser    
 ```
## Rebuilding a packed delta file
 Object 14120b refers to fa665c which refers to 07b8df.
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
