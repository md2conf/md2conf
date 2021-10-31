package io.github.md2conf.converter.markdown;

import io.github.md2conf.converter.PagesStructureProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.walk;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class MarkdownPagesStructureProvider implements PagesStructureProvider {

    private static final String MD_FILE_EXTENSION = "md";
    private static final String EXCLUDE_FILE_PREFIX = "."; //todo make exclude configurable
    //todo add confluence-content-model.json exclusions ?
    private static final String EXCLUDE_DIR_PREFIX = ".md2conf";
    private MarkdownPagesStructure structure;

    public MarkdownPagesStructure structure() {
        return structure;
    }

    public MarkdownPagesStructureProvider(Path rootDirectory) {
        structure = buildStructure(rootDirectory);

    }

    private MarkdownPagesStructure buildStructure(Path rootDirectory) {
        try {
            Map<Path, MarkdownPage> markdownPageIndex = indexMarkdownPagesByFolderPath(rootDirectory);
            List<MarkdownPage> allMarkdownPages = connectMarkdownPagesToParent(markdownPageIndex);
            List<MarkdownPage> topLevelMarkdownPages = findTopLevelMarkdownPages(allMarkdownPages, rootDirectory);
            return new MarkdownPagesStructure(topLevelMarkdownPages);
        } catch (IOException e) {
            throw new RuntimeException("Could not create markdown source structure", e);
        }
    }

    private List<MarkdownPage> connectMarkdownPagesToParent(Map<Path, MarkdownPage> markdownPageIndex) {
        markdownPageIndex.forEach((markdownPageFolderPath, markdownPage) -> {
            markdownPageIndex.computeIfPresent(markdownPage.path().getParent(), (ignored, parentMarkdownPage) -> {
                parentMarkdownPage.addChild(markdownPage);
                return parentMarkdownPage;
            });
        });

        return new ArrayList<>(markdownPageIndex.values());
    }

    private static Map<Path, MarkdownPage> indexMarkdownPagesByFolderPath(Path documentationRootFolder) throws IOException {
        return walk(documentationRootFolder)
                .filter((path) -> isMarkdownFile(path) && !isExcludeFile(path) && !isExcludeDir(path))
                .collect(toMap(MarkdownPagesStructureProvider::removeExtension, MarkdownPage::new));
    }

    private static List<MarkdownPage> findTopLevelMarkdownPages(List<MarkdownPage> markdownPageByFolderPath, Path documentationRootFolder) {
        return markdownPageByFolderPath.stream()
                                    .filter((markdownPage) -> markdownPage.path().equals(documentationRootFolder.resolve(markdownPage.path().getFileName())))
                                    .collect(toList());
    }

    private static Path removeExtension(Path path) {
        return Paths.get(path.toString().substring(0, path.toString().lastIndexOf('.')));
    }

    private static boolean isMarkdownFile(Path file) {
        return file.toString().endsWith(MD_FILE_EXTENSION);
    }

    private static boolean isExcludeFile(Path file) {
        return file.getFileName().toString().startsWith(EXCLUDE_FILE_PREFIX);
    }
    private static boolean isExcludeDir(Path file) {
        return file.toAbsolutePath().toString().contains(EXCLUDE_DIR_PREFIX);
    }
}
