---
title: Plantuml code macro Extension Spec
author:
version:
date: '2023-03-20'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...


## Plantuml code macro transformer

plantuml fenced code blocks treated as Plantuml code macros.

```````````````````````````````` example Plantuml code macro transformer: 1

```plantuml
a->b
```
.
{plantuml}
a->b
{plantuml}
.
Document[0, 21]
  PlantUmlCodeMacro[13, 18] lines[0]
````````````````````````````````


```````````````````````````````` example Plantuml code macro transformer: 2

```puml
a->b
```
.
{plantuml}
a->b
{plantuml}
.
Document[0, 17]
  PlantUmlCodeMacro[9, 14] lines[0]
````````````````````````````````


```````````````````````````````` example Plantuml code macro transformer: 3

```c4plantuml
a->b
```
.
{plantuml}
a->b
{plantuml}
.
Document[0, 23]
  PlantUmlCodeMacro[15, 20] lines[0]
````````````````````````````````