# GitBrowser
Just some experimental code to examine the files in the .git/objects folder. 
The documentation on the web is not entirely accurate.
## Example Output
```
Project .... GitBrowser

Objects ....      56
Commits ....      10
Trees ......      30
Blobs ......      16
Tags .......       0
Packfiles ..       1

Ndx  SHA-1   Type      Length
---  ------  ------  --------
  0  3ea331  COMMIT       252  
  1  6f2367  TREE          30  
  2  9e71e5  TREE          30  
  3  9e9cc3  TREE          37  
  4  32b187  TREE          37  
  5  51221a  BLOB       2,379  README.md 
  6  5fe6ec  COMMIT       266  
  7  d9922b  TREE          35  
  8  be7099  BLOB       7,309  GitBrowser.java 
  9  a239c7  BLOB       2,138  README.md 
 10  bdb293  TREE          67  
 11  ae2ad5  TREE          67  
 12  c78fb1  COMMIT       253  
 13  eea29b  BLOB      12,543  PackFileItem.java 
 14  f2256a  BLOB       6,325  GitBrowser.java 
 15  f5c924  BLOB       1,260  IndexFileItem.java 
 16  116c25  TREE          35  
 17  7cb6f5  TREE          35  
 18  8926d1  TREE          67  
 19  45f028  TREE         496  
 20  737dc9  TREE          67  
 21  87b722  COMMIT       268  
 22  9f2917  TREE          35  
 23  9fccb0  COMMIT       278  
 24  00b59e  TREE          30  
 25  9a44a2  BLOB       2,323  README.md 
 26  360d35  BLOB          89  README.md 
 27  5d314f  TREE         496  
 28  96d1bf  TREE          37  
 29  549979  TREE          30  
 30  98e8f7  COMMIT       263  
 31  53504d  TREE          37  
 32  301a2e  BLOB       8,162  PackFile.java 
 33  08cb83  TREE          67  
 34  6dbb03  COMMIT       252  
 35  397f66  TREE         496  
 36  99681f  BLOB       2,147  README.md 
 37  9719e8  COMMIT       267  
 38  908198  TREE          37  
 39  a0f767  BLOB       6,224  GitBrowser.java 
 40  ef3518  TREE          67  
 41  c324d7  TREE          30  
 42  f90b9f  BLOB       6,228  GitBrowser.java 
 43  fa0a59  COMMIT       258  
 44  c54984  COMMIT       254  
 45  f13bd0  TREE         496  
 46  79033f  TREE          30  
 47  48277b  BLOB       8,243  PackFile.java 
 48  70d324  TREE          67  
 49  1edbc7  BLOB       2,152  README.md 
 50  8554ee  TREE          30  
 51  76fe7d  BLOB       2,820  GitObject.java 
 52  1cfc45  BLOB       6,271  GitBrowser.java 
 53  47900e  TREE          35  
 54  78a9e9  TREE         496  
 55  25f33d  TREE          67  

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
