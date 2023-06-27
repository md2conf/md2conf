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
Document[0, 28]
  ConfluenceMacro[0, 18]
````````````````````````````````


Mix with other elements

```````````````````````````````` example Confluence macro transformer: 2
# Heading 

<!-- {jira:AAA-123} -->

```
text of code
```
.
h1. Heading

{jira:AAA-123}
{code}
text of code
{code}

.
Document[0, 57]
  Heading[0, 9] textOpen:[0, 1, "#"] text:[2, 9, "Heading"]
    Text[2, 9] chars:[2, 9, "Heading"]
  ConfluenceMacro[0, 14]
  FencedCodeBlock[37, 57] open:[37, 40, "```"] content:[41, 54] lines[1] close:[54, 57, "```"]
    Text[41, 54] chars:[41, 54, "text  … code\n"]
````````````````````````````````