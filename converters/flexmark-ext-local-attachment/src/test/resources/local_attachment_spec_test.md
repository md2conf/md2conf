---
title: Local Attachment Extension Spec
author: 
version:
date: '2023-01-23'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Local Attachmentlink transformer

Relative links to existing files resolved to LocalAttachmentLink

```````````````````````````````` example Local Attachmentlink transformer: 1
[test](src/test/resources/sample.txt)
.
[test|^sample.txt]

.
Document[0, 37]
  Paragraph[0, 37]
    LocalAttachmentLink[0, 37] textOpen:[0, 1, "["] text:[1, 5, "test"] textClose:[5, 6, "]"] linkOpen:[6, 7, "("] url:[7, 36, "src/test/resources/sample.txt"] linkClose:[36, 37, ")"]
      Text[1, 5] chars:[1, 5, "test"]
````````````````````````````````

Relative links with title to existing files resolved to LocalAttachmentLink

```````````````````````````````` example Local Attachmentlink transformer: 2
[test](src/test/resources/sample.txt "title")
.
[test|^sample.txt|title]

.
Document[0, 45]
  Paragraph[0, 45]
    LocalAttachmentLink[0, 45] textOpen:[0, 1, "["] text:[1, 5, "test"] textClose:[5, 6, "]"] linkOpen:[6, 7, "("] url:[7, 36, "src/test/resources/sample.txt"] titleOpen:[37, 38, "\""] title:[38, 43, "title"] titleClose:[43, 44, "\""] linkClose:[44, 45, ")"]
      Text[1, 5] chars:[1, 5, "test"]
````````````````````````````````


Relative links to non existing files resolved to Link

```````````````````````````````` example Local Attachmentlink transformer: 3
[test](src/test/resources/sample non exists)
.
[test](src/test/resources/sample non exists)

.
Document[0, 44]
  Paragraph[0, 44]
    LinkRef[0, 6] referenceOpen:[0, 1, "["] reference:[1, 5, "test"] referenceClose:[5, 6, "]"]
      Text[1, 5] chars:[1, 5, "test"]
    Text[6, 44] chars:[6, 44, "(src/ … ists)"]
````````````````````````````````

Absolute links to existing files resolved to LocalAttachmentLink  (This example works only for Unix)

```````````````````````````````` example Local Attachmentlink transformer: 4
[test](/etc/profile)
.
[test|^profile]

.
Document[0, 20]
  Paragraph[0, 20]
    LocalAttachmentLink[0, 20] textOpen:[0, 1, "["] text:[1, 5, "test"] textClose:[5, 6, "]"] linkOpen:[6, 7, "("] url:[7, 19, "/etc/profile"] linkClose:[19, 20, ")"]
      Text[1, 5] chars:[1, 5, "test"]
````````````````````````````````

Local Attachmentlink is a middle element of list item nodes chain

```````````````````````````````` example Local Attachmentlink transformer: 5
* **1** [test](src/test/resources/sample.txt) first item
.
* *1* [test|^sample.txt] first item

.
Document[0, 56]
  BulletList[0, 56] isTight
    BulletListItem[0, 56] open:[0, 1, "*"] isTight
      Paragraph[2, 56]
        StrongEmphasis[2, 7] textOpen:[2, 4, "**"] text:[4, 5, "1"] textClose:[5, 7, "**"]
          Text[4, 5] chars:[4, 5, "1"]
        Text[7, 8] chars:[7, 8, " "]
        LocalAttachmentLink[8, 45] textOpen:[8, 9, "["] text:[9, 13, "test"] textClose:[13, 14, "]"] linkOpen:[14, 15, "("] url:[15, 44, "src/test/resources/sample.txt"] linkClose:[44, 45, ")"]
          Text[9, 13] chars:[9, 13, "test"]
        Text[45, 56] chars:[45, 56, " firs …  item"]
````````````````````````````````


Local Attachmentlink is the last element of list item nodes chain

```````````````````````````````` example Local Attachmentlink transformer: 6
* **1** begin [test](src/test/resources/sample.txt) 
.
* *1* begin [test|^sample.txt]

.
Document[0, 52]
  BulletList[0, 52] isTight
    BulletListItem[0, 52] open:[0, 1, "*"] isTight
      Paragraph[2, 52]
        StrongEmphasis[2, 7] textOpen:[2, 4, "**"] text:[4, 5, "1"] textClose:[5, 7, "**"]
          Text[4, 5] chars:[4, 5, "1"]
        Text[7, 14] chars:[7, 14, " begin "]
        LocalAttachmentLink[14, 51] textOpen:[14, 15, "["] text:[15, 19, "test"] textClose:[19, 20, "]"] linkOpen:[20, 21, "("] url:[21, 50, "src/test/resources/sample.txt"] linkClose:[50, 51, ")"]
          Text[15, 19] chars:[15, 19, "test"]
````````````````````````````````

Local Attachmentlink is the first element of list item nodes chain

```````````````````````````````` example Local Attachmentlink transformer: 7
* [test](src/test/resources/sample.txt) a
.
* [test|^sample.txt] a

.
Document[0, 41]
  BulletList[0, 41] isTight
    BulletListItem[0, 41] open:[0, 1, "*"] isTight
      Paragraph[2, 41]
        LocalAttachmentLink[2, 39] textOpen:[2, 3, "["] text:[3, 7, "test"] textClose:[7, 8, "]"] linkOpen:[8, 9, "("] url:[9, 38, "src/test/resources/sample.txt"] linkClose:[38, 39, ")"]
          Text[3, 7] chars:[3, 7, "test"]
        Text[39, 41] chars:[39, 41, " a"]
````````````````````````````````
