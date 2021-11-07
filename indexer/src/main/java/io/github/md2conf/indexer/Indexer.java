package io.github.md2conf.indexer;

import java.nio.file.Path;

public interface Indexer {

    /**
     * Produce PagesStructure by indexing files in given path
     * @param path - input path
     * @return - PagesStructure object
     */
    PagesStructure indexPath(Path path);
}
