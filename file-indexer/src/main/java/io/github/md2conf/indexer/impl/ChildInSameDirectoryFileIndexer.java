package io.github.md2conf.indexer.impl;

import io.github.md2conf.indexer.DefaultPage;
import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class ChildInSameDirectoryFileIndexer extends AbstractFileIndexer {

    private static final List<String> PARENT_FILE_NAMES = List.of("index.md", "readme.md");
    private final Logger logger = LoggerFactory.getLogger(ChildInSameDirectoryFileIndexer.class);

    public ChildInSameDirectoryFileIndexer(FileIndexerConfigurationProperties fileIndexerConfigurationProperties) {
        super(fileIndexerConfigurationProperties);
    }

    @Override
    protected void logIgnored(List<Path> notIncludedToGraph) {
        logger.warn("Some paths ignored by indexer with child layout {} because it doesn't contain files with names {}",
                properties.getChildLayout(), PARENT_FILE_NAMES);
        notIncludedToGraph.forEach(
                v -> logger.warn("Ignore path {}", v)
        );
    }

    @Override
    protected List<DefaultPage> createPagesWithChildren(List<Path> pagePaths) throws IOException {
        List<DefaultPage> list = groupByDirectories(pagePaths);
        return establishParentChildRelation(list);
    }


    private List<DefaultPage> groupByDirectories(List<Path> pagePaths) throws IOException {
        return pagePaths.stream()
                .map(Path::getParent)
                .distinct()
                .map(this::toParentPageInDirectory)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    private DefaultPage toParentPageInDirectory(Path dir) {
        List<Path> list = fileList(dir);
        DefaultPage parentPageInList = findParentPage(list);
        if (parentPageInList != null) {
            //create pages and link the rest of files
            list.remove(parentPageInList.path());
            list.forEach(v -> parentPageInList.addChild(new DefaultPage(v)));
        }
        return parentPageInList;
    }

    private static DefaultPage findParentPage(List<Path> list) {
        DefaultPage res = null;
        Map<String, Path> pathMap = list.stream().collect(toMap(path -> path.getFileName().toString().toLowerCase(), Function.identity()));
        for (String key : PARENT_FILE_NAMES) {
            if (pathMap.containsKey(key)) {
                res = new DefaultPage(pathMap.get(key));
                break;
            }
        }
        return res;
    }

    private List<Path> fileList(Path dir) {
        try (Stream<Path> stream = Files.walk(dir, 1)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .filter(this::matchFileExtension)
                    .filter(this::isNotExcluded)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<DefaultPage> establishParentChildRelation(Collection<DefaultPage> pages) {
        List<DefaultPage> res = new ArrayList<>();
        Map<Path, DefaultPage> pageIndex = pages.stream()
                .collect(Collectors.toMap(v -> v.path().getParent(), Function.identity()));
        for (Path keyPath : pageIndex.keySet()) {
            if (pageIndex.containsKey(keyPath.getParent())) {
                pageIndex.get(keyPath.getParent()).addChild(pageIndex.get(keyPath));
            } else {
                logger.debug("Cannot find parent page for page with path {}", keyPath);
                res.add(pageIndex.get(keyPath));
            }
        }
        return res;
    }
}
