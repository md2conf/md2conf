* Status: accepted
* Deciders: qwazer
* Date: 2021 year

## Context and Problem Statement

1. Confluence doesn't have official tool to bulk publish markdown files to a Confluence instance.
   See https://community.atlassian.com/t5/Confluence-questions/Import-markdown-into-Confluence/qaq-p/211797 for details.
2. Existing tools for java developers are not meet my needs by different reasons.


* [confluence-publisher](https://github.com/confluence-publisher/confluence-publisher) - Maven plugin and Docker image to convert AsciiDoc and publish it to Confluence. Very great plugin with excellent confluence client.
* [bsorrentino/maven-confluence-plugin](https://github.com/bsorrentino/maven-confluence-plugin) - Maven plugin that generates project's documentation directly to confluence allowing to keep in-sync project evolution with its documentation
* [qwazer/markdown-confluence-gradle-plugin](https://github.com/qwazer/markdown-confluence-gradle-plugin) - Gradle plugin to publish markdown pages to confluence

## Considered Options

* Start this project
* Contribute to some existing projects


## Decision Outcome

Start this project.

Reasons why not to contribute to existing projects:

* The main problem with `bsorrentino/maven-confluence-plugin` that it has very complicated and hard-maintainable code. It hard to add new functionality.
* The main problem with `confluence-publisher` that it doesn't support markdown and it's not modularized properly. Hard to add own extension.


