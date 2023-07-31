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

CrosspageLink is a middle element of list item nodes chain

```````````````````````````````` example CrosspageLink transformer: 6
 * **1** [sample a](src/test/resources/dir%20a/sample%20space%20a.md) first item
 * **2** [sample a](src/test/resources/dir%20a/sample%20space%20a.md) second item
.
* *1* [sample a|Sample page A] first item
* *2* [sample a|Sample page A] second item

.
Document[0, 162]
  BulletList[1, 162] isTight
    BulletListItem[1, 81] open:[1, 2, "*"] isTight
      Paragraph[3, 81]
        StrongEmphasis[3, 8] textOpen:[3, 5, "**"] text:[5, 6, "1"] textClose:[6, 8, "**"]
          Text[5, 6] chars:[5, 6, "1"]
        Text[8, 9] chars:[8, 9, " "]
        CrosspageLink[9, 69] textOpen:[9, 10, "["] text:[10, 18, "sample a"] textClose:[18, 19, "]"] linkOpen:[19, 20, "("] url:[20, 68, "src/test/resources/dir%20a/sample%20space%20a.md"] title:[0, 13, "Sample page A"] linkClose:[68, 69, ")"]
          Text[10, 18] chars:[10, 18, "sample a"]
        Text[69, 80] chars:[69, 80, " firs …  item"]
    BulletListItem[82, 162] open:[82, 83, "*"] isTight
      Paragraph[84, 162]
        StrongEmphasis[84, 89] textOpen:[84, 86, "**"] text:[86, 87, "2"] textClose:[87, 89, "**"]
          Text[86, 87] chars:[86, 87, "2"]
        Text[89, 90] chars:[89, 90, " "]
        CrosspageLink[90, 150] textOpen:[90, 91, "["] text:[91, 99, "sample a"] textClose:[99, 100, "]"] linkOpen:[100, 101, "("] url:[101, 149, "src/test/resources/dir%20a/sample%20space%20a.md"] title:[0, 13, "Sample page A"] linkClose:[149, 150, ")"]
          Text[91, 99] chars:[91, 99, "sample a"]
        Text[150, 162] chars:[150, 162, " seco …  item"]
````````````````````````````````


CrosspageLink is the last element of list item nodes chain

```````````````````````````````` example CrosspageLink transformer: 7
 * **1** first item [sample a](src/test/resources/dir%20a/sample%20space%20a.md)
 * **2** second item [sample a](src/test/resources/dir%20a/sample%20space%20a.md)
.
* *1* first item [sample a|Sample page A]
* *2* second item [sample a|Sample page A]

.
Document[0, 162]
  BulletList[1, 162] isTight
    BulletListItem[1, 81] open:[1, 2, "*"] isTight
      Paragraph[3, 81]
        StrongEmphasis[3, 8] textOpen:[3, 5, "**"] text:[5, 6, "1"] textClose:[6, 8, "**"]
          Text[5, 6] chars:[5, 6, "1"]
        Text[8, 20] chars:[8, 20, " firs … item "]
        CrosspageLink[20, 80] textOpen:[20, 21, "["] text:[21, 29, "sample a"] textClose:[29, 30, "]"] linkOpen:[30, 31, "("] url:[31, 79, "src/test/resources/dir%20a/sample%20space%20a.md"] title:[0, 13, "Sample page A"] linkClose:[79, 80, ")"]
          Text[21, 29] chars:[21, 29, "sample a"]
    BulletListItem[82, 162] open:[82, 83, "*"] isTight
      Paragraph[84, 162]
        StrongEmphasis[84, 89] textOpen:[84, 86, "**"] text:[86, 87, "2"] textClose:[87, 89, "**"]
          Text[86, 87] chars:[86, 87, "2"]
        Text[89, 102] chars:[89, 102, " seco … item "]
        CrosspageLink[102, 162] textOpen:[102, 103, "["] text:[103, 111, "sample a"] textClose:[111, 112, "]"] linkOpen:[112, 113, "("] url:[113, 161, "src/test/resources/dir%20a/sample%20space%20a.md"] title:[0, 13, "Sample page A"] linkClose:[161, 162, ")"]
          Text[103, 111] chars:[103, 111, "sample a"]
````````````````````````````````

CrosspageLink is the first element of list item nodes chain

```````````````````````````````` example CrosspageLink transformer: 8
 * [sample a](src/test/resources/dir%20a/sample%20space%20a.md) a
 * [sample a](src/test/resources/dir%20a/sample%20space%20a.md) b
.
* [sample a|Sample page A] a
* [sample a|Sample page A] b

.
Document[0, 131]
  BulletList[1, 131] isTight
    BulletListItem[1, 66] open:[1, 2, "*"] isTight
      Paragraph[3, 66]
        CrosspageLink[3, 63] textOpen:[3, 4, "["] text:[4, 12, "sample a"] textClose:[12, 13, "]"] linkOpen:[13, 14, "("] url:[14, 62, "src/test/resources/dir%20a/sample%20space%20a.md"] title:[0, 13, "Sample page A"] linkClose:[62, 63, ")"]
          Text[4, 12] chars:[4, 12, "sample a"]
        Text[63, 65] chars:[63, 65, " a"]
    BulletListItem[67, 131] open:[67, 68, "*"] isTight
      Paragraph[69, 131]
        CrosspageLink[69, 129] textOpen:[69, 70, "["] text:[70, 78, "sample a"] textClose:[78, 79, "]"] linkOpen:[79, 80, "("] url:[80, 128, "src/test/resources/dir%20a/sample%20space%20a.md"] title:[0, 13, "Sample page A"] linkClose:[128, 129, ")"]
          Text[70, 78] chars:[70, 78, "sample a"]
        Text[129, 131] chars:[129, 131, " b"]
````````````````````````````````