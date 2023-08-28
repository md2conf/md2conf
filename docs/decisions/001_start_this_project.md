* Status: accepted
* Deciders: qwazer
* Date: 2021 year

## Context and Problem Statement

1. Confluence doesn't have official tool to bulk publish markdown files to a Confluence instance.
   See https://community.atlassian.com/t5/Confluence-questions/Import-markdown-into-Confluence/qaq-p/211797 for details.
2. Existing tools for java developers are not meet my needs by different reasons.


## Considered Options

* Start this project
* Contribute to some existing projects

* [confluence-publisher](https://github.com/confluence-publisher/confluence-publisher) - Maven plugin and Docker image to convert AsciiDoc and publish it to Confluence. Very great plugin with excellent confluence client.
* [bsorrentino/maven-confluence-plugin](https://github.com/bsorrentino/maven-confluence-plugin) - Maven plugin that generates project's documentation directly to confluence allowing to keep in-sync project evolution with its documentation
* [qwazer/markdown-confluence-gradle-plugin](https://github.com/qwazer/markdown-confluence-gradle-plugin) - Gradle plugin to publish markdown pages to confluence

## Decision Outcome

Start this project.

Reasons why not to contribute to existing projects:

* The main problem with `bsorrentino/maven-confluence-plugin` that is
  the adding of new functionality requires a lot of effort. Internal API
  and arhitecture is not clear. I tried to add idempotency to confluence
  client
  (see [maven-confluence-plugin/issues/233](https://github.com/bsorrentino/maven-confluence-plugin/issues/233)) but
  found that I need to rework almost everything in
  maven-confluence-plugin, becouse of a lot of tightly coupled
  functional style code.
* The main issue with `confluence-publisher` that it doesn't support
  markdown. It's not modularized properly. Hard to add own markdown extension.
* `markdown-confluence-gradle-plugin` - I'm author of this plugin :) It just obsolete.


