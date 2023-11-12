# GitBrowser
Just some experimental code to examine files in the .git folder.
## Example Output
### Project commit history
```
Project name ............ EnigmaMachine
Loose objects ........... 149
Pack files .............. 1
Pack items .............. 14

Branch: main

2023-09-28  changed error message
2023-09-04  added EntryDisk
2023-09-03  added test message
2023-08-31  tidying
2023-08-31  tidying
2023-08-30  tidying
2023-08-28  removed separate thin rotor
2023-08-27  M4
2023-08-27  removed thin types
2023-08-27  removing thin types
2023-08-27  attempt at thin rotors
2023-08-16  added Scrambler4Wheel
2023-08-15  added ThinReflector
2023-08-12  tidying
2023-08-12  privatised
2023-08-11  tidying
2023-08-09  tidying
2023-08-09  initial commit
```
### Contents of HEAD
```
dcd729  COMMIT       263
------------------------------------------------------------
Tree ....... 95004b
Parent ..... 510bad
Author ..... Denis Molony  2023-09-28 13:51
Committer .. Denis Molony  2023-09-28 13:51

changed error message

95004b  TREE          30
------------------------------------------------------------
86fae9  40000   src

86fae9  TREE          30
------------------------------------------------------------
ed5b49  40000   com

ed5b49  TREE          35
------------------------------------------------------------
c73e9e  40000   bytezone

c73e9e  TREE          33
------------------------------------------------------------
2d1826  40000   enigma

2d1826  TREE         385
------------------------------------------------------------
25db53  100644  BadWiringException.java
5b9eeb  100644  Enigma.java
17b627  100644  EntryDisk.java
0c11be  100644  PlugBoard.java
e0449d  100644  PlugBoardConnector.java
47f940  100644  Reflector.java
8ec280  100644  Rotor.java
52a7fb  100644  Scrambler.java
d2fe5e  100644  Wheel.java
```
