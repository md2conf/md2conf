package io.github.md2conf.indexer;

import static io.github.md2conf.indexer.FileIndexerConfigurationPropertiesBuilder.aFileIndexerConfigurationProperties;

public class FileIndexerConfigurationPropertiesFactory {

    public static FileIndexerConfigurationPropertiesBuilder aDefaultIndexerConfigurationProperties() {
        return aFileIndexerConfigurationProperties()
                .withFileExtension("wiki")
                .withChildLayout(ChildLayout.SUB_DIRECTORY)
                .withOrhanPagesStrategy(OrphanFileStrategy.IGNORE)
                .withExcludePattern("glob:**/.*");
    }
}
