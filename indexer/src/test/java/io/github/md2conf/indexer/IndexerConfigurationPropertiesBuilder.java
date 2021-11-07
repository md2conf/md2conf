package io.github.md2conf.indexer;

public final class IndexerConfigurationPropertiesBuilder {
    private String fileExtension = "wiki";
    private String includePattern = "glob:**";
    private String excludePattern = "glob:**/.*";

    private IndexerConfigurationPropertiesBuilder() {
    }

    public static IndexerConfigurationPropertiesBuilder anIndexerConfigurationProperties() {
        return new IndexerConfigurationPropertiesBuilder();
    }

    public IndexerConfigurationPropertiesBuilder withFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
        return this;
    }

    public IndexerConfigurationPropertiesBuilder withIncludePattern(String includePattern) {
        this.includePattern = includePattern;
        return this;
    }

    public IndexerConfigurationPropertiesBuilder withExcludePattern(String excludePattern) {
        this.excludePattern = excludePattern;
        return this;
    }

    public IndexerConfigurationProperties build() {
        IndexerConfigurationProperties indexerConfigurationProperties = new IndexerConfigurationProperties();
        indexerConfigurationProperties.setFileExtension(fileExtension);
        indexerConfigurationProperties.setIncludePattern(includePattern);
        indexerConfigurationProperties.setExcludePattern(excludePattern);
        return indexerConfigurationProperties;
    }
}
