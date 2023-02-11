package io.github.md2conf.converter;

import io.github.md2conf.indexer.PagesStructure;

import java.nio.file.Path;
import java.util.Map;

public interface PageStructureTitleProcessor {


    /**
     * Process pages structure to titleMap.
     * @param pagesStructure - pages structure
     * @return a Map, where key of titleMap is absolute path to file, value of titleMap is page title
     */
    Map<Path,String> toTitleMap(PagesStructure pagesStructure);


}
