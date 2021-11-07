package io.github.md2conf.indexer;

import static io.github.md2conf.indexer.IndexerConfigurationPropertiesBuilder.anIndexerConfigurationProperties;

public class IndexerConfigurationPropertiesFactory {

    public static IndexerConfigurationPropertiesBuilder aDefaultIndexerConfigurationProperties() {
        return anIndexerConfigurationProperties()
                .withFileExtension("wiki")
                .withIncludePattern("glob:**")
                .withExcludePattern("glob:**/.*");
    }
}
