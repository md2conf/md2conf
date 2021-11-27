package io.github.md2conf.indexer;

public class FileIndexerConfigurationProperties {

    private String fileExtension = "wiki";
    private String includePattern = "glob:**"; //todo drop - overcomplicated
    private String excludePattern = "glob:**/.*";

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

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public String toString() {
        return "FileIndexerConfigurationProperties{" +
                "fileExtension='" + fileExtension + '\'' +
                ", includePattern='" + includePattern + '\'' +
                ", excludePattern='" + excludePattern + '\'' +
                '}';
    }
}
