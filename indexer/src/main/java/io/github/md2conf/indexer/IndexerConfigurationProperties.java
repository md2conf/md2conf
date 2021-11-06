package io.github.md2conf.indexer;

public class IndexerConfigurationProperties {

    private String includePattern = "**";
    private String excludePattern = "**/.*";


    @Override
    public String toString() {
        return "IndexerConfigurationProperties{" +
                "includePattern='" + includePattern + '\'' +
                ", excludePattern='" + excludePattern + '\'' +
                '}';
    }
}
