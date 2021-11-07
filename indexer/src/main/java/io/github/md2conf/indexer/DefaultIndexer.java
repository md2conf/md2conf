package io.github.md2conf.indexer;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class DefaultIndexer implements Indexer {

    private final IndexerConfigurationProperties properties;
    private final PathMatcher includePathMatcher;
    private final PathMatcher excludePathMatcher;

    public DefaultIndexer(IndexerConfigurationProperties indexerConfigurationProperties) {
        this.properties = indexerConfigurationProperties;
        FileSystem fileSystem = FileSystems.getDefault();
        this.includePathMatcher = fileSystem.getPathMatcher(properties.getIncludePattern());
        this.excludePathMatcher = fileSystem.getPathMatcher(properties.getExcludePattern());
    }

    @Override
    public DefaultPagesStructure indexPath(Path rootPath) {
        try {
            Map<Path, DefaultPage> pageIndex = indexPages(rootPath);
            List<DefaultPage> allPages = linkPagesToParent(pageIndex);
            List<DefaultPage> topLevelPages = findTopLevelPages(allPages, rootPath);
            return new DefaultPagesStructure(topLevelPages);
        } catch (IOException e) {
            throw new RuntimeException("Could not index directory " + rootPath + "using properties" + properties, e);
        }
    }

//    //todo move to appropriate place
//    private ConfluenceContentModel createConfluenceContentModel(List<DefaultPage> topLevelPages) throws IOException {
//        List<ConfluencePage> confluencePages = new ArrayList<>();
//        for (DefaultPage topLevelPage : topLevelPages) { //use "for" loop to throw exception to caller
//            ConfluencePage confluencePage = createConfluencePage(topLevelPage);
//            confluencePages.add(confluencePage);
//        }
//        return new ConfluenceContentModel(confluencePages);
//    }
//
//    private ConfluencePage createConfluencePage(Page defaultPage) throws IOException {
//        ConfluencePage confluencePage = confluencePageFactory.pageByPath(defaultPage.path());
//        for (Page childPage : defaultPage.children()) {
//            ConfluencePage childConfluencePage = createConfluencePage(childPage);
//            confluencePage.getChildren().add(childConfluencePage);
//        }
//        return confluencePage;
//    }

    private Map<Path, DefaultPage> indexPages(Path rootPath) throws IOException {
        return Files.walk(rootPath)
                    .filter((path) -> isIncluded(path) && !isExcluded(path))
                    .collect(toMap(DefaultIndexer::removeExtension, DefaultPage::new));
    }

    private static Path removeExtension(Path path) {
        return Paths.get(path.toString().substring(0, path.toString().lastIndexOf('.')));
    }


    private List<DefaultPage> linkPagesToParent(Map<Path, DefaultPage> pageIndex) {
        pageIndex.forEach((absolutePath, page) -> {
            pageIndex.computeIfPresent(page.path().getParent(), (ignored, parentPage) -> {
                parentPage.addChild(page);
                return parentPage;
            });
        });
        return new ArrayList<>(pageIndex.values());
    }


    private static List<DefaultPage> findTopLevelPages(List<DefaultPage> allPages, Path rootPath) {
        return allPages.stream()
                       .filter((page) -> page.path().equals(rootPath.resolve(page.path().getFileName())))
                       .collect(toList());
    }

    private boolean isExcluded(Path path) {
       return excludePathMatcher.matches(path);
    }

    private boolean isIncluded(Path path) {
        return FilenameUtils.getExtension(path.toString()).equals(properties.getFileExtension())
                && includePathMatcher.matches(path);
    }


    static class DefaultPage implements Page {

        private final Path path;
        private final List<DefaultPage> children;

        DefaultPage(Path path) {
            this.path = path;
            this.children = new ArrayList<>();
        }

        @Override
        public Path path() {
            return path;
        }

        @Override
        public List<Page> children() {
            return unmodifiableList(this.children);
        }

        public void addChild(DefaultPage page) {
            this.children.add(page);
        }
    }

    static class DefaultPagesStructure implements PagesStructure{
        private final List<DefaultPage> pages;

        public DefaultPagesStructure(List<DefaultPage> pages) {
            this.pages = pages;
        }

        @Override
        public List<? extends Page> pages() {
            return pages;
        }
    }

}
