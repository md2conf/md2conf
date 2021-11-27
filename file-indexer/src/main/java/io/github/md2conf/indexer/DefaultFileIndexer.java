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

public class DefaultFileIndexer implements FileIndexer {

    private final FileIndexerConfigurationProperties properties;
    private final PathMatcher excludePathMatcher;

    public DefaultFileIndexer(FileIndexerConfigurationProperties fileIndexerConfigurationProperties) {
        this.properties = fileIndexerConfigurationProperties;
        FileSystem fileSystem = FileSystems.getDefault();
        this.excludePathMatcher = fileSystem.getPathMatcher(properties.getExcludePattern());
    }

    @Override
    public DefaultPagesStructure indexPath(Path rootPath) {
        try {
            Map<Path, DefaultPage> pageIndex = indexPages(rootPath);
            List<DefaultPage> allPages = linkPagesToParent(pageIndex);
            allPages.forEach(this::findAttachments);
            List<DefaultPage> topLevelPages = findTopLevelPages(allPages, rootPath);
            return new DefaultPagesStructure(topLevelPages);
        } catch (IOException e) {
            throw new RuntimeException("Could not index directory " + rootPath + "using properties" + properties, e);
        }
    }


    private Map<Path, DefaultPage> indexPages(Path rootPath) throws IOException {
        return Files.walk(rootPath)
                    .filter((path) -> isIncluded(path) && isNotExcluded(path))
                    .collect(toMap(DefaultFileIndexer::removeExtension,
                            DefaultPage::new));
    }

    private static Path removeExtension(Path path) {
        return Paths.get(path.toString().substring(0, path.toString().lastIndexOf('.')));
    }


    private void findAttachments(DefaultPage page) {
        Path attachmentsPath = attachmentsPath(page.path());
        if (!attachmentsPath.toFile().isDirectory()) {
            return;
        }
        List<Path> list = null;
        try {
            list = Files.walk(attachmentsPath(page.path()), 1)
                        .filter((path) ->  path.toFile().isFile() && isNotExcluded(path))
                        .collect(toList());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        page.attachments().addAll(list);
    }


    private static Path attachmentsPath(Path path) {
        return Path.of(removeExtension(path) + "_attachments");
    }


    private static List<DefaultPage> linkPagesToParent(Map<Path, DefaultPage> pageIndex) {
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

    private boolean isNotExcluded(Path path) {
        return !excludePathMatcher.matches(path);
    }

    private boolean isIncluded(Path path) {
        return FilenameUtils.getExtension(path.toString()).equals(properties.getFileExtension());
    }


    static class DefaultPage implements Page {

        private final Path path;
        private final List<DefaultPage> children;

        private final List<Path> attachments;

        DefaultPage(Path path) {
            this.path = path;
            this.children = new ArrayList<>();
            attachments = new ArrayList<>();
        }

        @Override
        public Path path() {
            return path;
        }

        @Override
        public List<Page> children() {
            return unmodifiableList(this.children);
        }

        @Override
        public List<Path> attachments() {
            return attachments;
        }

        public void addChild(DefaultPage page) {
            this.children.add(page);
        }
    }

    static class DefaultPagesStructure implements PagesStructure {
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
