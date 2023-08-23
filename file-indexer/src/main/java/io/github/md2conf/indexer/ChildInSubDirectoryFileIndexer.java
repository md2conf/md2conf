package io.github.md2conf.indexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class ChildInSubDirectoryFileIndexer extends AbstractFileIndexer {

    public ChildInSubDirectoryFileIndexer(FileIndexerConfigurationProperties fileIndexerConfigurationProperties) {
        super(fileIndexerConfigurationProperties);
    }

    @Override
    public List<DefaultPage> indexAndFindChildren(Path rootPath) throws IOException {
        Map<Path, DefaultPage> pageIndex = indexPages(rootPath);
        return establishParentChildRelation(pageIndex);
    }

    private Map<Path, DefaultPage> indexPages(Path rootPath) throws IOException {
        try (Stream<Path> stream = Files.walk(rootPath)) {
            return stream.filter(path -> isIncluded(path) && isNotExcluded(path))
                    .collect(toMap(PathNameUtils::removeExtension,
                            DefaultPage::new));
        }
    }

    private static List<DefaultPage> establishParentChildRelation(Map<Path, DefaultPage> pageIndex) {
        pageIndex.forEach((absolutePath, page) -> {
            pageIndex.computeIfPresent(page.path().getParent(), (ignored, parentPage) -> {
                parentPage.addChild(page);
                return parentPage;
            });
        });
        return new ArrayList<>(pageIndex.values());
    }



}
