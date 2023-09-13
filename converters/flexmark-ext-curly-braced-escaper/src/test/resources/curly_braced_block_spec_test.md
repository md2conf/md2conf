---
title: Curly braced block Extension Spec
author:
version:
date: '2023-03-20'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...


## Curly braced block transformer

curly braced text blocks escaped to avoid UnknownMacroMigrationException when posting to Confluence

```````````````````````````````` example Curly braced block transformer: 1
{abc}
.
\{abc\}

.
Document[0, 5]
  Paragraph[0, 5]
    CurlyBracedBlock[0, 5]
      Text[1, 4] chars:[1, 4, "abc"]
````````````````````````````````

curly braced text blocks escaped to avoid UnknownMacroMigrationException when posting to Confluence

```````````````````````````````` example Curly braced block transformer: 2
{{abc}}
.
\{\{abc\}\}

.
Document[0, 7]
  Paragraph[0, 7]
    CurlyBracedBlock[0, 7]
      CurlyBracedBlock[1, 6]
        Text[2, 5] chars:[2, 5, "abc"]
````````````````````````````````

