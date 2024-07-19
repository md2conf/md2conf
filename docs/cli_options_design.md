# CLI options design

## Elementary action (Steps)

| Action                     | Variant | Classes                                    |
|----------------------------|---------|--------------------------------------------|
| index                      |         | FileIndexer                                |
| saveConfluenceContentModel |         |                                            |
| loadConfluenceContentModel |         |                                            |
| convert                    | md2wiki | Md2WikiConverter                           |
| convert                    | view2md | View2MdConverter                           |
| publish                    |         | PublishConfluenceClient, ApiInternalClient |
| dump                       |         | DumpConfluenceClient, ApiInternalClient    |


## Commands

| Command         | Actions involved                                        | |
|-----------------|---------------------------------------------------------|-|
| convert md2wiki | index & saveConfluenceContentModel & convert            | |
| convert view2md | convert                                                 | | 
| publish         | loadConfluenceContentModel, publish                     | | 
| conpub          | index & saveConfluenceContentModel & convert &  publish | | 
| dump            | dump & saveConfluenceContentModel                       | | 
| dumpcon         | dump & saveConfluenceContentModel & convert view2md     | | 



## List of options

| option                       |   |  
|:-----------------------------|:--|
| outputDirectory              |   |
| inputDirectory               |   |
| converter                    |   |
| fileExtension                |   |
| excludePattern               |   |
| indexerRootPage              |   |
| childLayout                  |   |
| orphanFileStrategy           |   |
| titleExtract                 |   |
| titlePrefix                  |   |
| titleSuffix                  |   |
| titleChildPrefixed           |   |
| titleRemoveFromContent       |   |
| plantumlCodeMacroEnable      |   |
| plantumlCodeMacroName        |   |
| markdownRightMargin          |   |
| markdownHeadingStyle         |   |
| confluenceUrl                |   |
| username                     |   |
| password                     |   |
| spaceKey                     |   |
| parentPageTitle              |   |
| versionMessage               |   |
| orphanRemovalStrategy        |   |
| parentPagePublishingStrategy |   |
| notifyWatchers               |   |
| skipSslVerification          |   |
| maxRequestsPerSecond         |   |
| connectionTimeToLive         |   |