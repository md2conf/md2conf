---
title: Local Image Extension Spec
author: 
version:
date: '2023-08-12'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Local Image

Image with URL to existing file parsed as LocalImage and rendered as Confluence wiki image with filename.

```````````````````````````````` example Local Image: 1
![welcome.png](src/test/resources/welcome.png)
.
!welcome.png!

.
Document[0, 46]
  Paragraph[0, 46]
    LocalImage[0, 46] textOpen:[0, 2, "!["] text:[2, 13, "welcome.png"] textClose:[13, 14, "]"] linkOpen:[14, 15, "("] url:[15, 45, "src/test/resources/welcome.png"] linkClose:[45, 46, ")"]
      Text[2, 13] chars:[2, 13, "welco … e.png"]
````````````````````````````````


An image description used as the image's `alt` attribute.

Image titles are processed too

```````````````````````````````` example Local Image: 2
![Welcome to Confluence](src/test/resources/welcome.png)
.
!welcome.png|alt=Welcome to Confluence!

.
Document[0, 56]
  Paragraph[0, 56]
    LocalImage[0, 56] textOpen:[0, 2, "!["] text:[2, 23, "Welcome to Confluence"] textClose:[23, 24, "]"] linkOpen:[24, 25, "("] url:[25, 55, "src/test/resources/welcome.png"] linkClose:[55, 56, ")"]
      Text[2, 23] chars:[2, 23, "Welco … uence"]
````````````````````````````````


Image titles are processed too

```````````````````````````````` example Local Image: 3
![welcome](src/test/resources/welcome.png "Welcome to Confluence")
.
!welcome.png|title=Welcome to Confluence, alt=welcome!

.
Document[0, 66]
  Paragraph[0, 66]
    LocalImage[0, 66] textOpen:[0, 2, "!["] text:[2, 9, "welcome"] textClose:[9, 10, "]"] linkOpen:[10, 11, "("] url:[11, 41, "src/test/resources/welcome.png"] titleOpen:[42, 43, "\""] title:[43, 64, "Welcome to Confluence"] titleClose:[64, 65, "\""] linkClose:[65, 66, ")"]
      Text[2, 9] chars:[2, 9, "welcome"]
````````````````````````````````


Image with URL to non-existing file is usual Image

```````````````````````````````` example Local Image: 4
![foo](A.png)
.
!A.png!

.
Document[0, 13]
  Paragraph[0, 13]
    Image[0, 13] textOpen:[0, 2, "!["] text:[2, 5, "foo"] textClose:[5, 6, "]"] linkOpen:[6, 7, "("] url:[7, 12, "A.png"] pageRef:[7, 12, "A.png"] linkClose:[12, 13, ")"]
      Text[2, 5] chars:[2, 5, "foo"]
````````````````````````````````



Local Image is a middle element of list item nodes chain

```````````````````````````````` example Local Image: 5
* **1** ![test](src/test/resources/welcome.png) first item
.
* *1* !welcome.png|alt=test! first item

.
Document[0, 58]
  BulletList[0, 58] isTight
    BulletListItem[0, 58] open:[0, 1, "*"] isTight
      Paragraph[2, 58]
        StrongEmphasis[2, 7] textOpen:[2, 4, "**"] text:[4, 5, "1"] textClose:[5, 7, "**"]
          Text[4, 5] chars:[4, 5, "1"]
        Text[7, 8] chars:[7, 8, " "]
        LocalImage[8, 47] textOpen:[8, 10, "!["] text:[10, 14, "test"] textClose:[14, 15, "]"] linkOpen:[15, 16, "("] url:[16, 46, "src/test/resources/welcome.png"] linkClose:[46, 47, ")"]
          Text[10, 14] chars:[10, 14, "test"]
        Text[47, 58] chars:[47, 58, " firs …  item"]
````````````````````````````````


Local Image is the last element of list item nodes chain

```````````````````````````````` example Local Image: 6
* **1** begin ![test](src/test/resources/welcome.png)
.
* *1* begin !welcome.png|alt=test!

.
Document[0, 53]
  BulletList[0, 53] isTight
    BulletListItem[0, 53] open:[0, 1, "*"] isTight
      Paragraph[2, 53]
        StrongEmphasis[2, 7] textOpen:[2, 4, "**"] text:[4, 5, "1"] textClose:[5, 7, "**"]
          Text[4, 5] chars:[4, 5, "1"]
        Text[7, 14] chars:[7, 14, " begin "]
        LocalImage[14, 53] textOpen:[14, 16, "!["] text:[16, 20, "test"] textClose:[20, 21, "]"] linkOpen:[21, 22, "("] url:[22, 52, "src/test/resources/welcome.png"] linkClose:[52, 53, ")"]
          Text[16, 20] chars:[16, 20, "test"]
````````````````````````````````

Local Image is the first element of list item nodes chain

```````````````````````````````` example Local Image: 7
* ![test](src/test/resources/welcome.png) a
.
* !welcome.png|alt=test! a

.
Document[0, 43]
  BulletList[0, 43] isTight
    BulletListItem[0, 43] open:[0, 1, "*"] isTight
      Paragraph[2, 43]
        LocalImage[2, 41] textOpen:[2, 4, "!["] text:[4, 8, "test"] textClose:[8, 9, "]"] linkOpen:[9, 10, "("] url:[10, 40, "src/test/resources/welcome.png"] linkClose:[40, 41, ")"]
          Text[4, 8] chars:[4, 8, "test"]
        Text[41, 43] chars:[41, 43, " a"]
````````````````````````````````