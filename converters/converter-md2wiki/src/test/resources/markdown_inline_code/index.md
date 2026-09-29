# Inline code braces

Confluence scans inline code for macro and link syntax, so braces and brackets
must not leak through: `{toc}` and `{status:colour=Green|title=On track|subtle=true}`.

Brackets in inline code are escaped too: `[x]` and mixed `a{b}[c]`.
