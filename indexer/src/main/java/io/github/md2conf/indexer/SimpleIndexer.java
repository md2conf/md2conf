package io.github.md2conf.indexer;

import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class SimpleIndexer implements Indexer {

    private final IndexerConfigurationProperties properties;
    private final ConfluencePageFactory confluencePageFactory;

    private final PathMatcher includePathMatcher;
    private final PathMatcher excludePathMatcher;

    public SimpleIndexer(IndexerConfigurationProperties indexerConfigurationProperties) {
        this.properties = indexerConfigurationProperties;
        confluencePageFactory = new ConfluencePageFactory(properties.getExtractTitleStrategy());
        FileSystem fileSystem = FileSystems.getDefault();
        this.includePathMatcher = fileSystem.getPathMatcher(properties.getIncludePattern());
        this.excludePathMatcher = fileSystem.getPathMatcher(properties.getExcludePattern());
    }

    @Override
    public ConfluenceContentModel indexPath(Path rootPath) {
        try {
            Map<Path, ConfluencePage> pageIndex = indexPages(rootPath);
            List<ConfluencePage> allPages = linkPagesToParent(pageIndex);
            List<ConfluencePage> topLevelPages = findTopLevelPages(allPages, rootPath);
            return new ConfluenceContentModel(topLevelPages);
        } catch (IOException e) {
            throw new RuntimeException("Could not index directory " + rootPath + "Using properties" + properties, e);
        }
    }


    private Map<Path, ConfluencePage> indexPages(Path rootPath) throws IOException {
        return Files.walk(rootPath)
                    .filter((path) -> isIncluded(path) && !isExcluded(path))
                    .collect(toMap(Path::toAbsolutePath, confluencePageFactory::pageByPath));
    }


    private List<ConfluencePage> linkPagesToParent(Map<Path, ConfluencePage> pageIndex) {
        pageIndex.forEach((absolutePath, page) -> {
            pageIndex.computeIfPresent(absolutePathOfParentPage(page), (ignored, parentPage) -> {
                page.getChildren().add(page);
                return parentPage;
            });
        });
        return new ArrayList<>(pageIndex.values());
    }

    private Path absolutePathOfParentPage(ConfluencePage page) {
        return Path.of(Path.of(page.getContentFilePath()).getParent().toString()+ "."+properties.getFileExtension());
    }

    private static List<ConfluencePage> findTopLevelPages(List<ConfluencePage> allPages, Path rootPath) {
        return allPages.stream()
                       .filter((page) -> Path.of(page.getContentFilePath())
                                             .equals(rootPath.resolve(Path.of(page.getContentFilePath()).getFileName()))
                       )
                       .collect(toList());
    }

    private boolean isExcluded(Path path) {
        return false;

//        return excludePathMatcher.matches(path);
    }

    private boolean isIncluded(Path path) {
        return FilenameUtils.getExtension(path.toString()).equals(properties.getFileExtension())
                && includePathMatcher.matches(path);
    }


}
