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