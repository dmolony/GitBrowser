# GitBrowser
Just some experimental code to examine the files in the .git/objects folder. 
The documentation on the web is not entirely accurate.
## Example Output
```
Project .... GitBrowser

Objects ....      25
Commits ....       4
Trees ......      16
Blobs ......       5
Tags .......       0
Packfiles ..       1

Ndx   SHA-1   Type      Length
---   ------  ------  --------
  0   6f2367  TREE          30
  1   116c25  TREE          35
  2   8926d1  TREE          67
  3   87b722  COMMIT       268
  4   9f2917  TREE          35
  5   9fccb0  COMMIT       278
  6   00b59e  TREE          30
  7   360d35  BLOB          89
  8   5d314f  TREE         496
  9   96d1bf  TREE          37
 10   549979  TREE          30
 11   53504d  TREE          37
 12   397f66  TREE         496
 13   908198  TREE          37
 14   a0f767  BLOB       6,224
 15   ef3518  TREE          67
 16   c324d7  TREE          30
 17   f90b9f  BLOB       6,228
 18   fa0a59  COMMIT       258
 19   c54984  COMMIT       254
 20   79033f  TREE          30
 21   76fe7d  BLOB       2,820
 22   1cfc45  BLOB       6,271
 23   47900e  TREE          35
 24   78a9e9  TREE         496

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
