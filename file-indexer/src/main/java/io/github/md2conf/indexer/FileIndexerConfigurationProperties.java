package io.github.md2conf.indexer;

public class FileIndexerConfigurationProperties {

    private String fileExtension = "wiki"; //todo cwiki
    //maybe need to support several extensions in the same directory?
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
