package io.github.md2conf.indexer;

import io.github.md2conf.indexer.PagesStructureProvider.Page;
import io.github.md2conf.model.ConfluenceContentModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class SimpleIndexer implements Indexer{

    private final IndexerConfigurationProperties properties;

    public SimpleIndexer(IndexerConfigurationProperties indexerConfigurationProperties) {
        this.properties = indexerConfigurationProperties;
    }

    @Override
    public ConfluenceContentModel indexPath(Path rootPath) {
        try {
            Map<Path, Page> markdownPageIndex = indexPages(rootPath);
            List<Page> allPages = connectMarkdownPagesToParent(markdownPageIndex);
            List<Page> topLevelMarkdownPages = findTopLevelPages(allPages, rootPath);
            return new MarkdownPagesStructure(topLevelMarkdownPages);
        } catch (IOException e) {
            throw new RuntimeException("Could not index directory " + rootPath + "Using properties" + properties, e);
        }
    }

    private static ConfluenceContentModel convertToConfluenceContentModel(List<Page> pages){

    }


    private static Map<Path, Page> indexPages(Path rootPath) throws IOException {
        return Files.walk(rootPath)
                    .filter((path) -> isIncluded(path) && ! isExcluded(path))
                    .collect(toMap(Function.identity(), SimpleIndexer::newPage));
    }

    private List<Page> connectMarkdownPagesToParent(Map<Path, Page> pageIndex) {
        pageIndex.forEach((rootPath, page) -> {
            pageIndex.computeIfPresent(page.path().getParent(), (ignored, parentPage) -> {
                page.addChild(page);
                return parentPage;
            });
        });
        return new ArrayList<>(pageIndex.values());
    }

    private static List<Page> findTopLevelPages(List<Page> allPages, Path documentationRootFolder) {
        return allPages.stream()
                    .filter((markdownPage) -> markdownPage.path().equals(documentationRootFolder.resolve(markdownPage.path().getFileName())))
                    .collect(toList());
    }

    private static Page newPage(Path v) {
        return new Page() {
            @Override
            public Path path() {
                return v;
            }

            @Override
            public List<? extends Page> children() {
                return null;
            }
        };
    }

    private static boolean isExcluded(Path path) {
        return false; //todo implement
    }

    private static boolean isIncluded(Path path) {
        return true; //implement
    }


}
