package io.github.md2conf.indexer;

import static io.github.md2conf.indexer.IndexerConfigurationPropertiesBuilder.anIndexerConfigurationProperties;

public class IndexerConfigurationPropertiesFactory {

    public static IndexerConfigurationPropertiesBuilder aDefaultIndexerConfigurationProperties() {
        return anIndexerConfigurationProperties()
                .withFileExtension("wiki")
                .withIncludePattern("glob:**")
                .withIncludePattern("glob:**/.*")
                .withExtractTitleStrategy(ExtractTitleStrategy.FROM_FIRST_HEADER);
    }

    public static IndexerConfigurationPropertiesBuilder aTitleFromFilenameIndexerConfigurationProperties() {
        return anIndexerConfigurationProperties()
                .withFileExtension("wiki")
                .withIncludePattern("glob:**")
                .withExcludePattern("glob:**/.*")
                .withExtractTitleStrategy(ExtractTitleStrategy.FROM_FILENAME);
    }

    public static IndexerConfigurationPropertiesBuilder aTitleFromFirstHeaderIndexerConfigurationProperties() {
        return anIndexerConfigurationProperties()
                .withFileExtension("wiki")
                .withIncludePattern("glob:**")
                .withIncludePattern("glob:**/.*")
                .withExtractTitleStrategy(ExtractTitleStrategy.FROM_FIRST_HEADER);
    }
}
