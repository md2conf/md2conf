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


Relative links to existing files resolved to Link

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
