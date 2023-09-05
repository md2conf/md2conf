* Status: accepted
* Deciders: qwazer
* Date: 2022-01-20


## Context and Problem Statement

How to support diagramm from source code generation?


## Considered Options

1. Embedded diagram generation in the java app
2. Externalize diagram generation

## Decision Outcome

Don't include diagram generation in the java app.

Reasons:

1. Avoid tight-coupling. For example in case of PlantUML users should
   have capability to specify version of PlantUML that are used.
2. Follow UNIX principle “Do one thing, and do it well.”

From the other hand diagram generation is widely used, need to provde
docs how to setup digramm generation pipelines for common cases.
