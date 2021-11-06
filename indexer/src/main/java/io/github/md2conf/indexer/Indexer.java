package io.github.md2conf.indexer;

import io.github.md2conf.model.ConfluenceContentModel;

import java.nio.file.Path;

public interface Indexer {

    /**
     * Produce ConfluenceContentModel by indexing files in given path
     * @param path - input path
     * @return - ConfluenceContentModel object
     */
    ConfluenceContentModel indexPath(Path path);
}
