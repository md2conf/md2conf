package io.github.md2conf.indexer;

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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static io.github.md2conf.indexer.PathNameUtils.attachmentsDirectoryByPagePath;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class DefaultFileIndexer implements FileIndexer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultFileIndexer.class);

    private final FileIndexerConfigurationProperties properties;
    private final PathMatcher excludePathMatcher;

    public DefaultFileIndexer(FileIndexerConfigurationProperties fileIndexerConfigurationProperties) {
        this.properties = fileIndexerConfigurationProperties;
        FileSystem fileSystem = FileSystems.getDefault();
        this.excludePathMatcher = fileSystem.getPathMatcher(properties.getExcludePattern());
    }

    @Override
    public DefaultPagesStructure indexPath(Path rootPath) {
        final DefaultPagesStructure res;
        try {
            Map<Path, DefaultPage> pageIndex = indexPages(rootPath);
            List<DefaultPage> allPages = establishParentChildRelation(pageIndex);
            allPages.forEach(this::findAttachments);
            List<DefaultPage> topLevelPages = findTopLevelPages(allPages, rootPath);
            Optional<DefaultPage> rootPage = findRootPage(topLevelPages);
            if (rootPage.isPresent() && topLevelPages.size()>1){
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

    private static void relinkTopLevelPagesToRoot(DefaultPage rootPage, List<DefaultPage> topLevelPages) {
        topLevelPages.stream()
                .filter(v->!v.equals(rootPage))
                .forEach(rootPage::addChild);
    }

    private Optional<DefaultPage> findRootPage(List<DefaultPage> topLevelPages) {
        String rootPage = properties.getRootPage();
        if (properties.getRootPage()==null){
            return Optional.empty();
        }
        return topLevelPages.stream()
                .filter(p->p.path().getFileName().toString().equals(rootPage))
                .findFirst();
    }


    private Map<Path, DefaultPage> indexPages(Path rootPath) throws IOException {
        try (Stream<Path> stream = Files.walk(rootPath)){
            return stream.filter(path -> isIncluded(path) && isNotExcluded(path))
                    .collect(toMap(PathNameUtils::removeExtension,
                            DefaultPage::new));
        }
    }


    private void findAttachments(DefaultPage page) {
        Path attachmentsPath = attachmentsDirectoryByPagePath(page.path());
        if (!attachmentsPath.toFile().isDirectory()) {
            return;
        }
        List<Path> list;
        try (Stream<Path> stream = Files.walk(attachmentsDirectoryByPagePath(page.path()), 1)){
            list = stream.filter(path ->  path.toFile().isFile() && isNotExcluded(path))
                        .collect(toList());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        page.attachments().addAll(list);
    }



    public static List<DefaultPage> establishParentChildRelation(Map<Path, DefaultPage> pageIndex) {
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
                       .filter(page -> page.path().equals(rootPath.resolve(page.path().getFileName())))
                       .collect(toList());
    }

    private boolean isNotExcluded(Path path) {
        return !excludePathMatcher.matches(path);
    }

    private boolean isIncluded(Path path) {
        return FilenameUtils.getExtension(path.toString()).equals(properties.getFileExtension());
    }


}
