package io.github.md2conf.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PagesStructurePrinter {

    private Path basePath;
    private final Logger logger = LoggerFactory.getLogger(PagesStructurePrinter.class);

    public void prettyPrint(PagesStructure pagesStructure) {
        List<? extends Page> topLevelPages = new ArrayList<>(pagesStructure.pages()); //copy for sorting
        topLevelPages.sort(Comparator.comparing(Page::path));
        if (!topLevelPages.isEmpty()) {
            logger.info("Page structure is:");
            this.basePath = topLevelPages.get(0).path().getParent();
            String prefix = "";
            walk(topLevelPages, prefix);
        }
    }

    private void walk(List<? extends Page> list, String prefix) {
        Page page;
        list = new ArrayList<>(list);
        list.sort(Comparator.comparing(Page::path));
        for (int index = 0; index < list.size(); index++) {
            page = list.get(index);
            if (index == list.size() - 1) {
                logger.info("{}└── {}", prefix, basePath.relativize(page.path()));
                if (!page.children().isEmpty()) {
                    walk(page.children(), prefix + "    ");
                }
            } else {
                logger.info("{}├── {}", prefix, basePath.relativize(page.path()));
                if (!page.children().isEmpty()) {
                    walk(page.children(), prefix + "│   ");
                }
            }
        }
    }
}

