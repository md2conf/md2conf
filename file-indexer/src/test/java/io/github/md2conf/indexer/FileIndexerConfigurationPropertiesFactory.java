package io.github.md2conf.indexer;


public class FileIndexerConfigurationPropertiesFactory {

    public static FileIndexerConfigurationProperties.FileIndexerConfigurationPropertiesBuilder aDefaultIndexerConfigurationProperties() {
        return FileIndexerConfigurationProperties.builder()
                .fileExtension("wiki")
                .childLayout(ChildLayout.SUB_DIRECTORY)
                .orphanFileAction(OrphanFileAction.IGNORE)
                .excludePattern("glob:**/.*");
    }
}
