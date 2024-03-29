---
title: Custom Fenced code Extension Spec
author:
version:
date: '2023-03-20'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...


## Custom Fenced code transformer

Custom Fenced code

```````````````````````````````` example Custom Fenced code transformer: 1

```
basic code block
```
.
{code}
basic code block
{code}
.
Document[0, 25]
  CustomFencedCodeBlock[5, 22] lines[0]
````````````````````````````````


Custom Fenced code

```````````````````````````````` example Custom Fenced code transformer: 2

```json
{"a":"b"}
```
.
{code}
{"a":"b"}
{code}
.
Document[0, 22]
  CustomFencedCodeBlock[9, 19] info:[4, 8, "json"] lines[0]
````````````````````````````````


```````````````````````````````` example Custom Fenced code transformer: 3

```javascript
var a = 1;
```
.
{code:language=js}
var a = 1;
{code}
.
Document[0, 29]
  CustomFencedCodeBlock[15, 26] info:[4, 14, "javascript"] lines[0]
````````````````````````````````

Mix of code blocks with other elements


```````````````````````````````` example Custom Fenced code transformer: 4

# Header

```javascript
var a = 1;
```
> quote

```txt
txt
```

.
h1. Header

{code:language=js}
var a = 1;
{code}
{quote}
quote
{quote}

{code:language=text}
txt
{code}
.
Document[0, 65]
  Heading[1, 9] textOpen:[1, 2, "#"] text:[3, 9, "Header"]
    Text[3, 9] chars:[3, 9, "Header"]
  CustomFencedCodeBlock[25, 36] info:[14, 24, "javascript"] lines[0]
  BlockQuote[40, 48] marker:[40, 41, ">"]
    Paragraph[42, 48]
      Text[42, 47] chars:[42, 47, "quote"]
  CustomFencedCodeBlock[56, 60] info:[52, 55, "txt"] lines[0]
````````````````````````````````

Custom Fenced code block is the first element of a node chain

```````````````````````````````` example Custom Fenced code transformer: 5
```json
basic code block
```
text
.
{code}
basic code block
{code}
text

.
Document[0, 33]
  CustomFencedCodeBlock[8, 25] info:[3, 7, "json"] lines[0]
  Paragraph[29, 33]
    Text[29, 33] chars:[29, 33, "text"]
````````````````````````````````


Custom Fenced code block should preserve indentation

```````````````````````````````` example Custom Fenced code transformer: 6
```yaml
a:
  sub_a1: value
  sub_a2: value
```
text
.
{code:language=yml}
a:
  sub_a1: value
  sub_a2: value
{code}
text

.
Document[0, 51]
  CustomFencedCodeBlock[8, 43] info:[3, 7, "yaml"] lines[0]
  Paragraph[47, 51]
    Text[47, 51] chars:[47, 51, "text"]
````````````````````````````````