package io.github.md2conf.indexer;

import io.github.md2conf.indexer.impl.ChildInSameDirectoryFileIndexer;
import io.github.md2conf.indexer.impl.ChildInSubDirectoryFileIndexer;

import java.nio.file.Path;

public class DelegatingFileIndexer implements FileIndexer{

    FileIndexer fileIndexer;

    public DelegatingFileIndexer(FileIndexerConfigurationProperties properties) {
        switch (properties.getChildLayout()){
            case SAME_DIRECTORY:
                fileIndexer = new ChildInSameDirectoryFileIndexer(properties);
                break;
            case SUB_DIRECTORY:
                fileIndexer = new ChildInSubDirectoryFileIndexer(properties);
                break;
        }
    }

    @Override
    public PagesStructure indexPath(Path path) {
        return fileIndexer.indexPath(path);
    }
}
