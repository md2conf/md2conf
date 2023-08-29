package io.github.md2conf.indexer.impl;

import io.github.md2conf.indexer.DefaultPage;
import io.github.md2conf.indexer.DefaultPagesStructure;
import io.github.md2conf.indexer.FileIndexer;
import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import io.github.md2conf.indexer.OrphanFileStrategy;
import io.github.md2conf.indexer.Page;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.md2conf.indexer.PathNameUtils.attachmentsDirectoryByPagePath;
import static java.util.stream.Collectors.toList;

public abstract class AbstractFileIndexer implements FileIndexer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractFileIndexer.class);

    protected final FileIndexerConfigurationProperties properties;
    private final PathMatcher excludePathMatcher;

    public AbstractFileIndexer(FileIndexerConfigurationProperties fileIndexerConfigurationProperties) {
        this.properties = fileIndexerConfigurationProperties;
        FileSystem fileSystem = FileSystems.getDefault();
        this.excludePathMatcher = fileSystem.getPathMatcher(properties.getExcludePattern());
    }

    @Override
    public DefaultPagesStructure indexPath(Path rootPath) {
        final DefaultPagesStructure res;
        try {
            List<Path> pagePaths = pagePaths(rootPath);
            List<DefaultPage> allPages = createPagesWithChildren(pagePaths);
            List<DefaultPage> topLevelPages = findTopLevelPages(allPages, rootPath);
            processOrphans(pagePaths, topLevelPages);
            addAttachments(topLevelPages);
            Optional<DefaultPage> rootPage = findRootPage(topLevelPages);
            if (rootPage.isPresent() && topLevelPages.size() > 1) {
                relinkTopLevelPagesToRoot(rootPage.get(), topLevelPages);
                res = new DefaultPagesStructure(List.of(rootPage.get()));
            } else {
                res = new DefaultPagesStructure(topLevelPages);
            }
        } catch (IOException e) {
            logger.error("Could not index directory {} using properties {}", rootPath, properties);
            throw new RuntimeException(e);
        }
        return res;
    }

    private void addAttachments(List<? extends Page> list) {
        for (Page page : list){
            findAttachments(page);
            addAttachments(page.children());

        }

    }

    private void processOrphans(List<Path> pagePaths, List<DefaultPage> topLevelPages) {
        List<Path> notIncludedToGraph = notIncludedToGraph(topLevelPages, pagePaths);
        if (properties.getOrhanPagesStrategy() == OrphanFileStrategy.ADD_TO_TOP_LEVEL_PAGES) {
            notIncludedToGraph.forEach(v-> topLevelPages.add(new DefaultPage(v)));
        } else if (properties.getOrhanPagesStrategy().equals(OrphanFileStrategy.IGNORE)){
            if (!notIncludedToGraph.isEmpty()) {
                logIgnored(notIncludedToGraph);
            }
        }
    }

    protected abstract void logIgnored(List<Path> notIncludedToGraph);

    private static List<Path> notIncludedToGraph(List<DefaultPage> pagesGraph, List<Path> pagePaths) {
        List<Path> res = new ArrayList<>();
        List<Path> pathInGraph = new ArrayList<>();
        visitGraphNode(pathInGraph, pagesGraph);
        for (Path path : pagePaths){
            if (!pathInGraph.contains(path)){
                res.add(path);
            }
        }
        return res;
    }

    private static void visitGraphNode(List<Path> res, List<? extends Page> pagesGraph) {
        for (Page page : pagesGraph) {
            res.add(page.path());
            visitGraphNode(res, page.children());
        }
    }

    protected abstract List<DefaultPage> createPagesWithChildren(List<Path> pagePaths) throws IOException;

    private List<Path> pagePaths(Path rootPath) throws IOException {
        try (Stream<Path> stream = Files.walk(rootPath)) {
            return stream
                    .filter(path -> path.toFile().isFile())
                    .filter(this::matchFileExtension)
                    .filter(this::isNotExcluded)
                    .collect(Collectors.toList());
        }
    }

    private static void relinkTopLevelPagesToRoot(DefaultPage rootPage, List<DefaultPage> topLevelPages) {
        topLevelPages.stream()
                .filter(v -> !v.equals(rootPage))
                .forEach(rootPage::addChild);
    }

    private Optional<DefaultPage> findRootPage(List<DefaultPage> topLevelPages) {
        String rootPage = properties.getRootPage();
        if (properties.getRootPage() == null) {
            return Optional.empty();
        }
        return topLevelPages.stream()
                .filter(p -> p.path().getFileName().toString().equals(rootPage))
                .findFirst();
    }


    private void findAttachments(Page page) {
        Path attachmentsPath = attachmentsDirectoryByPagePath(page.path());
        if (!attachmentsPath.toFile().isDirectory()) {
            return;
        }
        List<Path> list;
        try (Stream<Path> stream = Files.walk(attachmentsDirectoryByPagePath(page.path()), 1)) {
            list = stream.filter(path -> path.toFile().isFile() && isNotExcluded(path))
                    .collect(toList());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        page.attachments().addAll(list);
    }

    private static List<DefaultPage> findTopLevelPages(List<DefaultPage> allPages, Path rootPath) {
        return allPages.stream()
                .filter(page -> page.path().equals(rootPath.resolve(page.path().getFileName())))
                .collect(toList());
    }

    protected boolean isNotExcluded(Path path) {
        return !excludePathMatcher.matches(path);
    }

    protected boolean matchFileExtension(Path path) {
        return FilenameUtils.getExtension(path.toString()).equals(properties.getFileExtension());
    }


}
