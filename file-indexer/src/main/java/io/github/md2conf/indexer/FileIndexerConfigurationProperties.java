package io.github.md2conf.indexer;

public class FileIndexerConfigurationProperties {

    private String fileExtension = "wiki";
    private String excludePattern = "glob:**/.*";

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
                ", excludePattern='" + excludePattern + '\'' +
                '}';
    }
}
