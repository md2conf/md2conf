# md2conf toolset

Toolset to convert and publish Markdown markup files to Confluence
instance. And download Confluence pages and save as markdown files on
filesystem.

This toolset designed for "docs-as-code" approach to use markdown as a
docs source and Confluence as a publishing platform.

## Tools

* **converter-markdown** - to convert *.md files to confluence wiki or
  storage format
* **confluence-client** - confluence client that utilize Confluence REST API
  for CRUD operation with content in a Confluence instance
* **confluence-dumper** -confluence client that dump given Confluence page with children pages


## confluence-client

**confluence-client** is a Java based confluence client that utilize
Confluence REST API to create/update/delete content in a Confluence
instance. It uses own domain model to describe Confluence content in
json file. It is a part of md2conf toolset.

### Confluence Content model

Confluence Content is a collection of Confluence Pages.


![confluence-content.png](doc/plantuml/confluence-content.png)

### Confluence Page

Confluence Page has next attributes

| Attribute       | Description                                |
|:----------------|:-------------------------------------------|
| title           | mandatory title                            |
| contentFilePath | mandatory content file path                |
| type            | "storage" or "wiki", see below for details |
| children        | optional collections of child pages        |
| attachments     | optional collections of attachments        |
| labels          | optional collections of labels             |

#### Content Type

Confluence support 2 types of markup "storage" or "wiki" to publish
pages using Confluence API. See Atlassian documentation for details:

* [Confluence Storage Format](https://confluence.atlassian.com/doc/confluence-storage-format-790796544.html)
  \- refered as "storage"
* [Confluence Wiki Markup](https://confluence.atlassian.com/doc/confluence-wiki-markup-251003035.html)
  \- refered as "wiki"

### Example

TODO

<!--TODO add example-->

### Usage

<!--TODO add usage-->

### History and Motivation

Originally written by Christian Stettler and others as part of
[confluence-publisher](https://github.com/confluence-publisher/confluence-publisher)
tool to publish ascii docs to confluence.

Forked to separate project to use as standalone tool in md2conf toolset.

Plan to add next features:

* Support for Confluence Wiki Markup content type
* Externalized configuration
* Command line interface
* Performance optimizations for idempotency feature


### License

Copyright (c) 2016-2021 Christian Stettler, Alain Sahli and others.

Copyright (c) 2021-, qwazer.
