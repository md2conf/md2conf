# Contributor guide

md2conf is a set of tools to publish markdown files to Confluence and to dump Confluence content back into markdown.
Stack: Java 11, Maven (multi-module), picocli, flexmark, Lombok, SLF4J + Logback, JUnit 5 + AssertJ + Mockito, JaCoCo.

## Project and module layout

| Module                     | Purpose                                                                                                                                                                                                                                                                                |
|:---------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `confluence-content-model` | Confluence content model (pages, attachments)                                                                                                                                                                                                                                          |
| `file-indexer`             | walks a markdown directory following naming conventions → `PagesStructure`                                                                                                                                                                                                             |
| `converters/*`             | converters (`converter-md2wiki`, `converter-view2md`, `converter-copying`, `converter-noop`), shared `PageStructureConverter` / `ConfluenceModelConverter` interfaces in `converter-common`, plus `title-processor`, `markdown-formatter` and the flexmark `flexmark-ext-*` extensions |
| `confluence-client`        | REST client with idempotent publishing                                                                                                                                                                                                                                                 |
| `md2conf-command`          | CLI (picocli), entry point `io.github.md2conf.command.MainApp`                                                                                                                                                                                                                         |
| `md2conf-jar`              | shaded single-jar assembly                                                                                                                                                                                                                                                             |
| `md2conf-maven-plugin`     | Maven plugin (mojos for `conpub`, `dump`, `dumpcon`, `convert`)                                                                                                                                                                                                                        |
| `docs/`                    | requirements and ADRs (`docs/decisions/`, template `_template.md`)                                                                                                                                                                                                                     |

Sources live in `src/main/java`, unit tests in `src/test/java`, integration tests in `src/it/java`
(added to test-sources by `build-helper-maven-plugin`), test fixtures in `src/it/resources`.
Packages: `io.github.md2conf.<module>`.

## Build, test and local run commands

```bash
mvn install                                             # build all modules + unit tests
mvn -pl file-indexer -am install -DskipTests            # fast build of one module with its dependencies
mvn -pl file-indexer test -Dtest=PathNameUtilsTest      # single test class (also supports #method)
java -jar md2conf-jar/target/md2conf*.jar help          # run the CLI locally
```

Integration tests require a running Confluence on port 8090:

```bash
docker run -d -p8090:8090 -p8091:8091 --name=atlassian-sdk-confluence qwazer/atlassian-sdk-confluence:latest
./waitForConfluenceStartup.sh atlassian-sdk-confluence 300
mvn -B install -Pintegration-tests                      # exactly what CI runs (.github/workflows/build.yml)
mvn -pl md2conf-maven-plugin -P plugin-it failsafe:integration-test failsafe:verify   # plugin ITs
```

## Code style and naming

- Formatting: 4 spaces, braces follow the surrounding code; there is no shared formatter/linter config in the repo — match neighbouring files.
- Lombok (`@Value`, `@Builder`) is accepted; log only via SLF4J; console output goes through picocli.
- A new CLI option is added to the corresponding picocli command and must be documented in the properties table in `README.md`.
- Core domain conventions are enforced by the indexer code: attachments in `<page-name>_attachments/`, child layout `SUB_DIRECTORY` / `SAME_DIRECTORY`, a `.md2conf_skipupdate` file suppresses page updates.

## Testing

- Unit tests: `**/*Test.java` under `src/test/java` (surefire); assert expectations with AssertJ.
- flexmark extension tests are named `**/*SpecTest.java` — "markdown in → expected output out".
- Integration tests: `**/*IntegrationTest.java` under `src/it/java` (failsafe, `integration-tests` profile); plugin ITs are `**/*PluginIT.java` (`plugin-it` profile). Surefire excludes both.
- Coverage is collected by JaCoCo during `verify` and uploaded to Codecov; pipeline coverage is a required part of CI.
- Any change to converter or indexer logic comes with a test — these modules are covered by resource-driven tests over `src/it/resources`.

## VCS: commits and pull requests

- Default branch is `master`; land changes only via PRs into it (`develop` is a scratch branch).
- Commit subject: short lowercase imperative, to the point: `fix title header`, `drop unused field`.
- Issue-driven change — prefix the issue key and keep the PR number in the subject: `md2conf-286 fix no Exit code on failure (#303)`.
- Dependency updates are left to Dependabot with `Bump <artifact> from X to Y` subjects — do not rename them.
- PR description: what changes and why, link the issue; for CLI changes include a command example and update `README.md`; for architecture decisions add an ADR as `docs/decisions/NNN_*.md`.
- Before opening a PR: `mvn -B install -Pintegration-tests` is green with the Confluence container up.

## Versioning and release

The version is derived from git by `maven-git-versioning-extension` (`.mvn/`): a branch → `<branch>-SNAPSHOT`, a `v1.2.3` tag → release.
Do not edit `<version>` in `pom.xml` and do not hand-edit `.git-versioned-pom.xml` — both are generated; the release procedure is in `RELEASE.md`.
