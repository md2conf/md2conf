# confluence-client

**confluence-client** is a Java based conluence client that utilize
Confluence REST API to create/update/delete content in a Confluence
instance.  
It uses own domain model to describe Confluence content in json file. It
is a part of md2conf toolset.

### Conluence Content model

Conluence Content is a list of confluence pages. Each page has mandatory
attributes title, content file path. Optional content type can be
specified. A page can have optional childen, attachments, labels.

![confluence-content.png](doc/plantuml/confluence-content.png)

#### Content Type

Confluence support 2 types of markup to publish pages using Confluence
API.

* [Confluence Storage Format](https://confluence.atlassian.com/doc/confluence-storage-format-790796544.html) - refered as "storage"
* [Confluence Wiki Markup](https://confluence.atlassian.com/doc/confluence-wiki-markup-251003035.html) - refered as "wiki"

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
* Performance optimizations for indepotency feature


### License

Copyright (c) 2017-2021 Christian Stettler and others.

Copyright (c) 2021-, qwazer.
