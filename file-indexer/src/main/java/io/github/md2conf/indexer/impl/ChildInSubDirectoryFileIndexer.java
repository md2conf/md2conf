package io.github.md2conf.indexer.impl;

import io.github.md2conf.indexer.DefaultPage;
import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import io.github.md2conf.indexer.PathNameUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class ChildInSubDirectoryFileIndexer extends AbstractFileIndexer {

    private final Logger logger = LoggerFactory.getLogger(ChildInSubDirectoryFileIndexer.class);

    public ChildInSubDirectoryFileIndexer(FileIndexerConfigurationProperties fileIndexerConfigurationProperties) {
        super(fileIndexerConfigurationProperties);
    }

    @Override
    protected void logIgnored(List<Path> notIncludedToGraph) {
        logger.warn("Some paths ignored by indexer with child layout {}",
                properties.getChildLayout());
        notIncludedToGraph.forEach(
                v -> logger.warn("Ignored path {} because file at parent path {} doesn't exist", v, parentPath(v))
        );
    }

    private String parentPath(Path v) {
        String fileName =  FilenameUtils.getBaseName(v.getParent().toString()) + "." + properties.getFileExtension();
        if (v.getParent().getParent()!=null){
            return v.getParent().getParent().resolve(fileName).toString();
        }
        return fileName;
    }

    @Override
    public List<DefaultPage> createPagesWithChildren(List<Path> pagePaths) {
        Map<Path, DefaultPage> pageIndex = pagePaths.stream()
                .collect(toMap(PathNameUtils::removeExtension,
                        DefaultPage::new));
        return establishParentChildRelation(pageIndex);
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
