package io.github.md2conf.indexer;

public class IndexerConfigurationProperties {

    private String fileExtension = "wiki";
    private String includePattern = "glob:**";
    private String excludePattern = "glob:**/.*";

    private ExtractTitleStrategy extractTitleStrategy = ExtractTitleStrategy.FROM_FIRST_HEADER;

    public String getIncludePattern() {
        return includePattern;
    }

    public void setIncludePattern(String includePattern) {
        this.includePattern = includePattern;
    }

    public String getExcludePattern() {
        return excludePattern;
    }

    public void setExcludePattern(String excludePattern) {
        this.excludePattern = excludePattern;
    }

    public ExtractTitleStrategy getExtractTitleStrategy() {
        return extractTitleStrategy;
    }

    public void setExtractTitleStrategy(ExtractTitleStrategy extractTitleStrategy) {
        this.extractTitleStrategy = extractTitleStrategy;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public String toString() {
        return "IndexerConfigurationProperties{" +
                "fileExtension='" + fileExtension + '\'' +
                ", includePattern='" + includePattern + '\'' +
                ", excludePattern='" + excludePattern + '\'' +
                ", extractTitleStrategy=" + extractTitleStrategy +
                '}';
    }
}
