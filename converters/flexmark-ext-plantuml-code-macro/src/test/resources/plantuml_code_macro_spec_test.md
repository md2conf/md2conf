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
Document[0, 20]
  PlantUmlCodeMacro[12, 17] lines[0]
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

Mix of plantuml code and other macros

```````````````````````````````` example Plantuml code macro transformer: 4

# Sample code blocks

```puml
a->b
```

```json
{"a":1}
```
.
h1. Sample code blocks

{plantuml}
a->b
{plantuml}
{code:lang=json}
{"a":1}
{code}

.
Document[0, 60]
  Heading[1, 21] textOpen:[1, 2, "#"] text:[3, 21, "Sample code blocks"]
    Text[3, 21] chars:[3, 21, "Sampl … locks"]
  PlantUmlCodeMacro[31, 36] lines[0]
  FencedCodeBlock[41, 60] open:[41, 44, "```"] info:[44, 48, "json"] content:[49, 57] lines[1] close:[57, 60, "```"]
    Text[49, 57] chars:[49, 57, "{\"a\":1}\n"]
````````````````````````````````

plantuml fenced code blocks is first element of node chain

```````````````````````````````` example Plantuml code macro transformer: 5
```plantuml
a->b
```
text
.
{plantuml}
a->b
{plantuml}
text

.
Document[0, 25]
  PlantUmlCodeMacro[12, 17] lines[0]
  Paragraph[21, 25]
    Text[21, 25] chars:[21, 25, "text"]
````````````````````````````````