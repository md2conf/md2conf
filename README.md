[![maven-central](https://img.shields.io/maven-central/v/io.github.md2conf/md2conf-command.svg)](https://search.maven.org/artifact/io.github.md2conf/md2conf-command)
[![codecov](https://codecov.io/gh/md2conf/md2conf/branch/master/graph/badge.svg?token=PJEAQ8SXH4)](https://codecov.io/gh/md2conf/md2conf)

## Overview

Set of command-line tools to publish markdown files to a Confluence or
dump Confluence content as markdown files with attachments.

Notable features:

- Automatically index input directory to build confluence content model
  based on file name conventions.
- Idempotence confluence client. Avoid limitation of Confluence REST
  API, that create a new version of a page on every update.
- Support cross-page links between markdown pages, inline images, etc.
- Dump Confluence pages and convert to local markdown files keeping links
  to images and links between pages in local markdown files. This is unique feature among similar tools.
- Extensible by design

This toolset designed to support "docs-as-code" approach to use markdown
as a docs source and Confluence as a publishing platform. The "dump" functionality allow to easy create initial docs in
markdown based on a Confluence content.

## Installation

Download the latest release from maven central

### Play locally

Need to have: `java` and `docker` in your PATH, an input directory with
markdown files.

Start Confluence locally:

```bash
docker run -p 8090:8090 -p 8091:8091 qwazer/atlassian-sdk-confluence
```

After Confluence starts it will be accessible at http://localhost:8090
with `admin:admin` credentials.

Run next command

```bash
java -jar md2conf.jar conpub -i=main-application/src/it/resources/several-pages --username=admin --password=admin --space-key=ds -pt="Welcome to Confluence" -url=http://localhost:8090
```

See results at http://localhost:8090/display/ds/Sample

### Publish to remote Confluence instance

Change `url`, `space-key`, `parent-page-title`, `username`, `password`
in the command above and run.

## Usage

### Command-line

```
Usage: md2conf [-v] [COMMAND]
Set of tools to deal with markdown files and Confluence: publish, dump, convert
  -v, --verbose   Increase verbosity.
Commands:
  conpub, convert-and-publish  Convert and publish docs to a Confluence instance
  convert                      Convert
  dump                         Dump content from Confluence instance and save
                                 as 'confluence-content-model' with files in
                                 Confluence VIEW format
  dumpcon, dump-and-convert    Dump content from Confluence instance, convert
                                 using VIEW2MD converter to directory tree with
                                 markdown files and binary attachments
  index                        Index input directory to build page structure
                                 and print results
  publish                      Publish content to a Confluence instance
  help                         Display help information about the specified
                                 command.
```

### Docker

Run to read the help message

```
docker run md2conf/md2conf:latest help
```

Mount current working dir with "docs" directory and publish content to a remote confluence.

```
docker run -v ./docs:/docs md2conf/md2conf conpub -i=/docs --username=admin --password=admin --space-key=ds -pt="Welcome to Confluence" -url=http://confluence.local
```

Mount current working dir with "docs" directory and dump content from a remote confluence.

```
docker run -v ./docs:/docs md2conf/md2conf dumpcon -o=/docs --username=admin --password=admin --space-key=ds -pt="Welcome to Confluence" -url=http://confluence.local
```

### Maven plugin

```xml

<plugin>
    <groupId>io.github.md2conf</groupId>
    <artifactId>md2conf-maven-plugin</artifactId>
    <!--            <version>SPECIFY version here</version>-->
    <executions>
        <execution>
            <goals>
                <goal>conpub</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <inputDirectory>docs/markdown</inputDirectory>
        <indexerRootPage>index.md</indexerRootPage>
        <titleChildPrefixed>true</titleChildPrefixed>
        <confluenceUrl>https://some-confluence-url</confluenceUrl>
        <username>admin</username>
        <password>admin</password>
        <spaceKey>DS</spaceKey>
        <parentPageTitle>Welcome to Confluence</parentPageTitle>
        <skipSslVerification>true</skipSslVerification>
    </configuration>
</plugin>
```

## How it works inside?

Main publish-and-convert steps are

1. Index input directory and build page structure based on file name
   conventions. Each page is a prototype for future Confluence Page.
   Page represented by file path, attachments and children pages.
2. Convert page structure to Confluence Content Model with set of
   Confluence Pages. Each Confluence Page receive confluence-specific
   attributes like "title", "labels" and "type" ("storage" or "wiki").
3. Publish Confluence Content Model to a Confluence Instance via
   Confluence REST API.

![main-publish-steps.png](docs/plantuml/main-publish-steps.png)

Main dump-and-convert steps are

1. Dump Confluence Content Model identified by user-provided parent page
   to temp working directory `.md2conf`
2. Convert it using MD2VIEW converter. MD2VIEW converter build on top of
   `flexmark` `html2md` converter.

![dump-convert-steps.png](docs/plantuml/dump-convert-steps.png)

### Index by file-indexer

File-indexer is a tool that build Confluence Content Model based on file
name conventions.

File-indexer controlled by properties:

| Property key            | CLI name                                   | Description                                                                                                                                                                                                                                                                                                      | Default value |
|:------------------------|:-------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------|
| inputDirectory          | "-i", "--input-dir", "--indexer-input-dir" | Input directory                                                                                                                                                                                                                                                                                                  |               |
| indexerFileExtension    | --indexer-file-extension                   | File extension to index as confluence content pages                                                                                                                                                                                                                                                              | md            |
| indexerExcludePattern   | --indexer-exclude-pattern                  | Exclude pattern in format of glob:** or regexp:.*. For syntax see javadoc of java.nio.file.FileSystem.getPathMatcher method                                                                                                                                                                                      | "glob:**/.*"  |
| indexerRootPage         | --indexer-root-page                        | Use specified page as parent page for all another top-level pages in an input directory                                                                                                                                                                                                                          |               |
| indexerChildLayout      | --indexer-child-layout                     | SUB_DIRECTORY is layout when source files for children pages resides in directory with the name equals to basename of parent file. SAME_DIRECTORY is layout when file with name 'index.md' or 'README.md' is the source file of parent page and other files in the directory are source files for children pages | SUB_DIRECTORY |
| indexerOrphanFileAction | --indexer-orphan-file-action               | What to do with page which source file that are not top-level page and not child of any page. Possible options are IGNORE, ADD_TO_TOP_LEVEL_PAGES                                                                                                                                                                | IGNORE        |

#### Attachments naming convention

Attachment file of page `page.md` must be located in directory which
name is concatenation of basename of parent page and "_attachments"
suffix (`./page_attachments`).

For example, next filesystem tree

```
.
├── page_attachments
│   └── attach.txt
└── page.md
```

will be indexed as Confluence page with source at path `./page.md` and
one attachment at path `page_attachments/attach.txt`.

#### Child relation layout examples

There are 2 options to specify `indexerChildLayout`: SUB_DIRECTORY and
SAME_DIRECTORY

##### SUB_DIRECTORY

This is layout when source files for children pages resides in directory
with the name equals to basename of parent file.

Example:

Next files tree

```
├── page_a
│   └─── child_to_page_a.md
└── page_a.md
```

will be indexed to next pages structure

```
└── page_a.md
   └─── page_a/child_to_page_a.md
```

##### SAME_DIRECTORY

This is layout when file with name 'index.md' or 'README.md' is the
source file of parent page and other files in the directory are source
files for children pages.

Next files tree

```
.
├── index.md
├── page_a.md
└── page_b.md
```

will be indexed to next pages structure

```
└── index.md
   ├─── page_a.md
   └─── page_b.md
```

#### Property "indexerOrphanFileAction"

When `indexerOrphanFileAction` set to `ADD_TO_TOP_LEVEL_PAGES` the next files tree

```
├── some_dir
│   └─── orphan.md
└── page_a.md
```

will be indexed to next pages structure

```
├─── page_a.md
└─── some_dir/orphan.md
```

#### Property "indexerRootPage"

When `indexerRootPage` set to `overview.md` the next files tree

```
├─── overview.md
├─── page_a.md
└─── page_b.md
```

will be indexed to next pages structure

```
└── overview.md
   ├─── page_a.md
   └─── page_b.md
```

### Convert by converters

Controlled by properties:

| Property key    | CLI name             | Description                                               | Default value |
|:----------------|:---------------------|:----------------------------------------------------------|:--------------|
| outputDirectory | "-o", "--output-dir" | Output directory                                          |               |


#### Title processing options

| Property key           | CLI name                     | Description                                                                                                                                                                            | Default value     |
|:-----------------------|:-----------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------|
| titleExtract           | --title-extract              | Strategy to extract title from file, FROM_FIRST_HEADER or FROM_FILENAME                                                                                                                | FROM_FIRST_HEADER |
| titlePrefix            | --title-prefix               | Title prefix common for all pages                                                                                                                                                      |                   |
| titleSuffix            | --title-suffix               | Title suffix common for all pages                                                                                                                                                      |                   |
| titleChildPrefixed     | --title-child-prefixed       | Add title prefix of root page if page is a child                                                                                                                                       | false             |
| titleRemoveFromContent | --title-remove-from-content  | Remove title from converted content, to avoid duplicate titles rendering in an Confluence                                                                                              | false             |


#### Markdown to Wiki Converter properties (MD2WIKI)

| Property key           | CLI name                     | Description                                                                                                                                                                            | Default value     |
|:-----------------------|:-----------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------|
| plantumlCodeAsMacro    | --plantuml-code-macro-enable | Render markdown plantuml fenced code block as confluence plantuml macro (server-side rendering)                                                                                        | false             |
| plantumlCodeMacroName  | --plantuml-code-macro-name   | Name of confluence macro to render plantuml. Need to Confluence plugin. Possible known options are: 'plantuml' or 'plantumlrender' or 'plantumlcloud'. By default, 'plantuml' is used. | plantuml          |

The result of conversion saved in output directory file
`confluence-content-model.json`'.

#### View to Markdown Converter properties (VIEW2MD)

There are properties to format markdown produced by VIEW2MD converter.

| Property key         | CLI name                 | Description                                                                   | Default value |
|:---------------------|:-------------------------|:------------------------------------------------------------------------------|:--------------|
| markdownRightMargin  | --markdown-right-margin  | Markdown right margin size                                                    | 120           |
| markdownHeadingStyle | --markdown-heading-style | Markdown heading style. Valid values:  AS_IS, ATX_PREFERRED, SETEXT_PREFERRED | ATX_PREFERRED |

### Confluence connection options

Controlled by properties:

| Property key         | CLI name                     | Description                                                                                                     | Default value |
|:---------------------|:-----------------------------|:----------------------------------------------------------------------------------------------------------------|:--------------|
| confluenceUrl        | "-url", "--confluence-url"   | The root URL of the Confluence instance                                                                         |               |
| username             | "--username"                 | Username of the Confluence user                                                                                 |               |
| password             | "--password"                 | The password or personal access token of the user. In case of using token don't specify username.               |               |
| spaceKey             | "-s", "--space-key"          | The password or personal access token of the user                                                               |               |
| parentPageTitle      | "-pt", "--parent-page-title" | The parent page to publish `confluence-content-model`                                                           |               |
| skipSslVerification  | --skip-ssl-verification      |                                                                                                                 | false         |
| maxRequestsPerSecond | --max-requests-per-second    |                                                                                                                 |               |
| connectionTimeToLive | --connection-time-to-live    | Connection TTL. Useful in case a server is configured to have a very low TTL to keep existing connectings alive |               |

### Publish

Controlled by properties:

| Property key                 | CLI name                           | Description                                             | Default value                    |
|:-----------------------------|:-----------------------------------|:--------------------------------------------------------|:---------------------------------|
| orphanRemovalStrategy        | --orphan-removal-strategy          | REMOVE_ORPHANS or KEEP_ORPHANS                          | KEEP_ORPHANS                     |
| parentPagePublishingStrategy | --parent-page-publishing-strategy  | APPEND_TO_ANCESTOR or REPLACE_ANCESTOR                  | APPEND_TO_ANCESTOR               |
| notifyWatchers               | --notify-watchers                  |                                                         | false                            |
| versionMessage               | --version-message                  |                                                         | Published by md2conf             |
| confluenceContentModelPath   | "-m", "--confluence-content-model" | Path to file with `confluence-content-model` JSON file. | '.confluence-content-model.json' |

### Dump

For Confluence Content model dump need to provide [Confluence connection options](#confluence-connection-options) and
output directory.

### Confluence Content model

Confluence Content is a collection of Confluence Pages. It represented
in file `confluence-content-model.json` in local filesystem.

![confluence-content.png](docs/plantuml/confluence-content.png)

#### Confluence Page

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
  \- referred as "storage"
* [Confluence Wiki Markup](https://confluence.atlassian.com/doc/confluence-wiki-markup-251003035.html)
  \- referred as "wiki"

Additional VIEW html-like format used to render pages. It used only in
dump functionality.

## Markdown extensions

### Confluence macros

Html inline comments with <!-- { } --> treated as Confluence macros. For
list of Confluence macros see
https://support.atlassian.com/confluence-cloud/docs/what-are-macros/ and
section "Wiki markup example" on particular macros. A couple of useful
are
[table-of-contents-macro](https://support.atlassian.com/confluence-cloud/docs/insert-the-table-of-contents-macro/)
and
[children-display-macro](https://support.atlassian.com/confluence-cloud/docs/insert-the-children-display-macro/)

Also see
[confluence_macro_spec_test](converters/flexmark-ext-confluence-macro/src/test/resources/confluence_macro_spec_test.md)

### Cross-page links between markdown pages

In case of markdown file has content that refer to another markdown
file, that exists in page structure the reference will be converted to
valid Confluence page reference.

For examples see
[crosspage_link_title_spec_test](converters/flexmark-ext-crosspage-link/src/test/resources/crosspage_link_title_spec_test.md)

### Image attachments

Markdown image will be converted to a Confluence Image. If markdown
image has link to local file, it will be converted to Confluence
attachment and uploaded to a Confluence server.

For examples see
[local_image_spec_test](converters/flexmark-ext-local-image/src/test/resources/local_image_spec_test.md)

### Link to local file

A link to local file (both relative and absolute) will be converted to a
Confluence attachment link. The target link file will be uploaded as
Confluence attachment.

Note: Using absolute paths with links is not recommended.

For examples see
[local_attachment_spec_test](converters/flexmark-ext-local-attachment/src/test/resources/local_attachment_spec_test.md)

### Confluence supported languages in fenced code blocks

Markdown fenced code block converted to Confluence code block macro.
Confluence code block macro supports only limited number of languages.
For example, it doesn't support "json" language. To avoid this
limitation some fenced code block languages remaped to supported by an
Confluence.

See mappings here:
[CustomFencedCodeBlockRenderer.java: Line 22](converters/flexmark-ext-fenced-code-block/src/main/java/io/github/md2conf/flexmart/ext/fenced/code/block/internal/CustomFencedCodeBlockRenderer.java#L22)

## Diagram generations

The `md2conf` toolset doesn't contain embedded diagram generation support from textual diagram formats like PlantUML,
Mermaid, etc.
Users can setup own diagram generators in their pipelines.

### Example to include PlantUML diagram generation in Maven

This setup use Apache Maven as orchestrator for diagram generation and for docs publishing.

Next setup will convert all "*.puml" files from "plantuml" directory and saves output in "markdown" directory.
Actual version of PlantUML should be specified by a user.

```xml

<plugin>
    <groupId>com.github.jeluard</groupId>
    <artifactId>plantuml-maven-plugin</artifactId>
    <version>1.2</version>
    <configuration>
        <sourceFiles>
            <directory>plantuml</directory>
            <includes>
                <include>**/*.puml</include>
            </includes>
        </sourceFiles>
        <outputDirectory>markdown</outputDirectory>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>net.sourceforge.plantuml</groupId>
            <artifactId>plantuml</artifactId>
            <version>1.2023.10</version>
        </dependency>
    </dependencies>
</plugin>
```

## Advanced usage

### Skip publishing update with `.md2conf_skipupdate` file

It is possible to ignore publishing page updates. It is usefull when a page structure maintained in source code layout,
but some page content updated manually not by docs-as-code approach.
Such pages can be defined in `.md2conf_skipupdate` file.
Rules defined the same way as in `.gitignore` file. 

Example of `.md2conf_skipupdate` file:

```
# ignore publishing page updates for ./feedback.md
./feedback.md
```

## History and motivation

See [decisions](docs/decisions) and [comparison with other tools](docs/comparison_with_other_tools.md).

In short, existing projects doesn't fit my needs.

### Regards

Idempotence confluence client originally written by Christian Stettler
and others as part of
[confluence-publisher](https://github.com/confluence-publisher/confluence-publisher)
tool to publish ascii docs to confluence.

### License

Copyright (c) 2016-2021 Christian Stettler, Alain Sahli and others.

Copyright (c) 2021-, qwazer.
