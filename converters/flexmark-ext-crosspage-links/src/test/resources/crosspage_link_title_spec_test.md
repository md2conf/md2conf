---
title: Crosspage link Extension Spec
author: 
version:
date: '2023-02-05'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## CrosspageLink transformer

Relative links to existing pages resolved to CrossPageLink

```````````````````````````````` example CrosspageLink transformer: 1
[test](src/test/resources/sample_page.md)
.
[test|sample_page]

.
Document[0, 41]
  Paragraph[0, 41]
    CrosspageLink[0, 41] textOpen:[0, 1, "["] text:[1, 5, "test"] textClose:[5, 6, "]"] linkOpen:[6, 7, "("] url:[7, 40, "src/test/resources/sample_page.md"] title:[0, 11, "sample_page"] linkClose:[40, 41, ")"]
      Text[1, 5] chars:[1, 5, "test"]
````````````````````````````````


Relative links to existing pages with empty text resolved to CrossPageLink

```````````````````````````````` example CrosspageLink transformer: 2
[](src/test/resources/sample_page.md)
.
[sample_page]

.
Document[0, 37]
  Paragraph[0, 37]
    CrosspageLink[0, 37] textOpen:[0, 1, "["] text:[1, 1] textClose:[1, 2, "]"] linkOpen:[2, 3, "("] url:[3, 36, "src/test/resources/sample_page.md"] title:[0, 11, "sample_page"] linkClose:[36, 37, ")"]
````````````````````````````````

In case of same link text and link url rendered only page title

```````````````````````````````` example CrosspageLink transformer: 3
[src/test/resources/sample_page.md](src/test/resources/sample_page.md)
.
[sample_page]

.
Document[0, 70]
  Paragraph[0, 70]
    CrosspageLink[0, 70] textOpen:[0, 1, "["] text:[1, 34, "src/test/resources/sample_page.md"] textClose:[34, 35, "]"] linkOpen:[35, 36, "("] url:[36, 69, "src/test/resources/sample_page.md"] title:[0, 11, "sample_page"] linkClose:[69, 70, ")"]
      Text[1, 34] chars:[1, 34, "src/t … ge.md"]
````````````````````````````````


Link to non-existing page is not CrosspageLink

```````````````````````````````` example CrosspageLink transformer: 4
[link_text](/tmp/sample.txt)
.
[link_text|/tmp/sample.txt]

.
Document[0, 28]
  Paragraph[0, 28]
    Link[0, 28] textOpen:[0, 1, "["] text:[1, 10, "link_text"] textClose:[10, 11, "]"] linkOpen:[11, 12, "("] url:[12, 27, "/tmp/sample.txt"] pageRef:[12, 27, "/tmp/sample.txt"] linkClose:[27, 28, ")"]
      Text[1, 10] chars:[1, 10, "link_text"]
````````````````````````````````


Link to existing page with space in name in subdirectory with space in name

```````````````````````````````` example CrosspageLink transformer: 5
[sample a](src/test/resources/dir%20a/sample%20space%20a.md)
.
[sample a|Sample page A]

.
Document[0, 60]
  Paragraph[0, 60]
    CrosspageLink[0, 60] textOpen:[0, 1, "["] text:[1, 9, "sample a"] textClose:[9, 10, "]"] linkOpen:[10, 11, "("] url:[11, 59, "src/test/resources/dir%20a/sample%20space%20a.md"] title:[0, 13, "Sample page A"] linkClose:[59, 60, ")"]
      Text[1, 9] chars:[1, 9, "sample a"]
````````````````````````````````