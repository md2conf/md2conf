package io.github.md2conf.indexer;

public final class FileIndexerConfigurationPropertiesBuilder {
    private String fileExtension;
    private String excludePattern;
    private String rootPage;
    private ChildLayout childLayout;

    private FileIndexerConfigurationPropertiesBuilder() {
    }

    public static FileIndexerConfigurationPropertiesBuilder aFileIndexerConfigurationProperties() {
        return new FileIndexerConfigurationPropertiesBuilder();
    }

    public FileIndexerConfigurationPropertiesBuilder withFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
        return this;
    }

    public FileIndexerConfigurationPropertiesBuilder withExcludePattern(String excludePattern) {
        this.excludePattern = excludePattern;
        return this;
    }

    public FileIndexerConfigurationPropertiesBuilder withRootPage(String rootPage) {
        this.rootPage = rootPage;
        return this;
    }

    public FileIndexerConfigurationPropertiesBuilder withChildLayout(ChildLayout childLayout) {
        this.childLayout = childLayout;
        return this;
    }

    public FileIndexerConfigurationProperties build() {
        FileIndexerConfigurationProperties fileIndexerConfigurationProperties = new FileIndexerConfigurationProperties();
        fileIndexerConfigurationProperties.setFileExtension(fileExtension);
        fileIndexerConfigurationProperties.setExcludePattern(excludePattern);
        fileIndexerConfigurationProperties.setRootPage(rootPage);
        fileIndexerConfigurationProperties.setChildLayout(childLayout);
        return fileIndexerConfigurationProperties;
    }
}
