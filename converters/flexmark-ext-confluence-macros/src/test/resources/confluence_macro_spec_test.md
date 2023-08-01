---
title: Confluence macro Extension Spec
author:
version:
date: '2023-03-20'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...


## Confluence macro transformer

Html inline comments with <!-- { } --> treated as Confluence macros.

```````````````````````````````` example Confluence macro transformer: 1
<!-- {children:depth=3} -->
.
{children:depth=3}
.
Document[0, 27]
  ConfluenceMacro[0, 18]
````````````````````````````````


Any number of spaces can be used in after HTML comment starts and before HTML comment ends

```````````````````````````````` example Confluence macro transformer: 2
<!--     {toc}         -->
### Heading 

.
{toc}
h3. Heading

.
Document[0, 41]
  ConfluenceMacro[0, 5]
  Heading[27, 38] textOpen:[27, 30, "###"] text:[31, 38, "Heading"]
    Text[31, 38] chars:[31, 38, "Heading"]
````````````````````````````````


ConfluenceMacro is a middle element of list item nodes chain

```````````````````````````````` example Confluence macro transformer: 3
 * **1** <!-- {jira:AAA-1} --> first item
 * **2** <!-- {jira:AAA-2} --> second item
.
* *1* {jira:AAA-1} first item
* *2* {jira:AAA-2} second item

.
Document[0, 84]
  BulletList[1, 84] isTight
    BulletListItem[1, 42] open:[1, 2, "*"] isTight
      Paragraph[3, 42]
        StrongEmphasis[3, 8] textOpen:[3, 5, "**"] text:[5, 6, "1"] textClose:[6, 8, "**"]
          Text[5, 6] chars:[5, 6, "1"]
        Text[8, 9] chars:[8, 9, " "]
        ConfluenceMacro[0, 12]
        Text[30, 41] chars:[30, 41, " firs …  item"]
    BulletListItem[43, 84] open:[43, 44, "*"] isTight
      Paragraph[45, 84]
        StrongEmphasis[45, 50] textOpen:[45, 47, "**"] text:[47, 48, "2"] textClose:[48, 50, "**"]
          Text[47, 48] chars:[47, 48, "2"]
        Text[50, 51] chars:[50, 51, " "]
        ConfluenceMacro[0, 12]
        Text[72, 84] chars:[72, 84, " seco …  item"]
````````````````````````````````


ConfluenceMacro is the last element of list item nodes chain

```````````````````````````````` example Confluence macro transformer: 4
 * **1** first item <!-- {jira:AAA-1} -->
 * **2** second item <!-- {jira:AAA-2} -->
.
* *1* first item {jira:AAA-1}
* *2* second item {jira:AAA-2}

.
Document[0, 84]
  BulletList[1, 84] isTight
    BulletListItem[1, 42] open:[1, 2, "*"] isTight
      Paragraph[3, 42]
        StrongEmphasis[3, 8] textOpen:[3, 5, "**"] text:[5, 6, "1"] textClose:[6, 8, "**"]
          Text[5, 6] chars:[5, 6, "1"]
        Text[8, 20] chars:[8, 20, " firs … item "]
        ConfluenceMacro[0, 12]
    BulletListItem[43, 84] open:[43, 44, "*"] isTight
      Paragraph[45, 84]
        StrongEmphasis[45, 50] textOpen:[45, 47, "**"] text:[47, 48, "2"] textClose:[48, 50, "**"]
          Text[47, 48] chars:[47, 48, "2"]
        Text[50, 63] chars:[50, 63, " seco … item "]
        ConfluenceMacro[0, 12]
````````````````````````````````

ConfluenceMacro is the first element of list item nodes chain

```````````````````````````````` example Confluence macro transformer: 5
 * <!-- {jira:AAA-1} --> a
 * <!-- {jira:AAA-2} --> b
.
* {jira:AAA-1}
* {jira:AAA-2}

.
Document[0, 53]
  BulletList[1, 53] isTight
    BulletListItem[1, 27] open:[1, 2, "*"] isTight
      ConfluenceMacro[0, 12]
    BulletListItem[28, 53] open:[28, 29, "*"] isTight
      ConfluenceMacro[0, 12]
````````````````````````````````