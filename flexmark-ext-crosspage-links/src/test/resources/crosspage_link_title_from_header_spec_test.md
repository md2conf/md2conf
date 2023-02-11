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
[test|SAMPLE PAGE HEADER]

.
Document[0, 41]
  Paragraph[0, 41]
    CrosspageLink[0, 41] textOpen:[0, 1, "["] text:[1, 5, "test"] textClose:[5, 6, "]"] linkOpen:[6, 7, "("] url:[7, 40, "src/test/resources/sample_page.md"] linkClose:[40, 41, ")"]
      Text[1, 5] chars:[1, 5, "test"]
````````````````````````````````


Relative links to existing pages without text resolved to CrossPageLink

```````````````````````````````` example CrosspageLink transformer: 2
[](src/test/resources/sample_page.md)
.
[SAMPLE PAGE HEADER]

.
Document[0, 37]
  Paragraph[0, 37]
    CrosspageLink[0, 37] textOpen:[0, 1, "["] text:[1, 1] textClose:[1, 2, "]"] linkOpen:[2, 3, "("] url:[3, 36, "src/test/resources/sample_page.md"] linkClose:[36, 37, ")"]
````````````````````````````````

Same link text and link url rendered only page title

```````````````````````````````` example CrosspageLink transformer: 3
[src/test/resources/sample_page.md](src/test/resources/sample_page.md)
.
[SAMPLE PAGE HEADER]

.
Document[0, 70]
  Paragraph[0, 70]
    CrosspageLink[0, 70] textOpen:[0, 1, "["] text:[1, 34, "src/test/resources/sample_page.md"] textClose:[34, 35, "]"] linkOpen:[35, 36, "("] url:[36, 69, "src/test/resources/sample_page.md"] linkClose:[69, 70, ")"]
      Text[1, 34] chars:[1, 34, "src/t … ge.md"]
````````````````````````````````