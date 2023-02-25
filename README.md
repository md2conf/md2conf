[![status](https://img.shields.io/badge/STATUS-DEVELOPING-important)](https://img.shields.io/badge/STATUS-DEVELOPING-important)
[![codecov](https://codecov.io/gh/md2conf/md2conf/branch/master/graph/badge.svg?token=PJEAQ8SXH4)](https://codecov.io/gh/md2conf/md2conf)

# md2conf toolset

Set of tools to publish markdown files to a Confluence.

Notable features:

- Support attachments, inline images, etc.
- Can be used in minimal configuration. In this mode directory structure and naming conventions used to build a page tree with confluence content model.
- Has a lot of advanced configuration options
- Idempotent publish (without creating of new version of pages in Confluence if nothing changed)
- Extensible by design

This toolset designed to support "docs-as-code" approach to use markdown
as a docs source and Confluence as a publishing platform.


## Installation

Download latest release from maven central

### Play locally

Need to have:  java and docker in your PATH, an input directory with markdown files.

Start Confluence locally:

```
docker run -p 8090:8090 -p 8091:8091 qwazer/atlassian-sdk-confluence
```
After Confluence start it will be accessible at http://localhost:8090 with admin:admin credentials.


Run this command

```
java md2conf-cli
```


## Parts of md2conf toolset

* **confluence-content-model** - an abstraction to model confluence
  content on a filesystem.
* **converters** - tools to convert directories with files to
  `confluence-content-model` or from `confluence-content-model`
* **confluence-client** - confluence client that utilize Confluence REST
  API for CRUD operation with content in a Confluence instance.


### Confluence Content model

Confluence Content is a collection of Confluence Pages.


![confluence-content.png](docs/plantuml/confluence-content.png)

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

## confluence-client

**confluence-client** is a Java based confluence client that utilize
Confluence REST API to create/update/delete content in a Confluence
instance. It uses own domain model to describe Confluence content in
json file. It is a part of md2conf toolset.



### Example

TODO

<!--TODO add example-->

## Usage

### Command-line

```
Usage: md2conf [-v] [COMMAND]
Set of tools to work with 'confluence-content-model': publish, dump, convert.
  -v, --verbose   Increase verbosity.
Commands:
  convert                      Convert files to `confluence-content-model` or
                                 from `confluence-content-model`
  publish                      Publish content to a Confluence instance
  conpub, convert-and-publish  Convert and publish docs to a Confluence instance
  dump                         Dump content from Confluence instance
  help                         Displays help information about the specified
                                 command
'confluence-content-model' is a representation of Confluence page trees and
attachments on a local filesystem. See 'md2conf help model' for details.
```

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
