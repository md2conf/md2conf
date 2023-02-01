package io.github.md2conf.flexmart.ext.crosspage.links.internal;

import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeTracker;
import com.vladsch.flexmark.util.data.DataHolder;
import io.github.md2conf.flexmart.ext.crosspage.links.CrosspageLink;
import io.github.md2conf.flexmart.ext.crosspage.links.CrosspageLinkExtension;
import io.github.md2conf.indexer.DefaultFileIndexer;
import io.github.md2conf.indexer.Page;
import io.github.md2conf.indexer.PagesStructure;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrosspageLinkPostProcessor extends NodePostProcessor {
    private final Path currentFilePath;
    private final Set<Path> pagesPathes;

    public CrosspageLinkPostProcessor(Path currentFilePath,
                                      Set<Path> pagesPathes) {
        this.currentFilePath = currentFilePath;
        this.pagesPathes = pagesPathes;
    }

    @Override
    public void process(@NotNull NodeTracker state, @NotNull Node node) {
        if (node instanceof Link){
            String url = ((Link) node).getUrl().toString();
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return;
            }
            if (!url.endsWith(".md")) {
                return;
            }
            if (currentFilePath==null){
                return;
            }
            Path relative = currentFilePath.resolve(url);
            Path absolute = Path.of(url);
            final Path resolvedPath;
            if (isRegularFileExists(relative)) {
                resolvedPath = relative;
            } else if (isRegularFileExists(absolute)) {
                resolvedPath = absolute;
            } else {
                return;
            }
            if (pagesPathes.contains(resolvedPath.toAbsolutePath())){
                Node parent = node.getParent();
                CrosspageLink crosspageLink = new CrosspageLink((Link) node);
                crosspageLink.setPath(resolvedPath);
                crosspageLink.takeChildren(node);
                node.unlink();
                if (parent != null) {
                    parent.appendChild(crosspageLink);
                }
                state.nodeRemoved(node);
                state.nodeAddedWithChildren(crosspageLink);
            }
        }
    }

    public static boolean isRegularFileExists(Path path) {
        return Files.exists(path) && Files.isRegularFile(path);
    }


    public static class Factory extends NodePostProcessorFactory {

        private final Path currentFilePath;
        private final PagesStructure pagesStructure;

        public Factory(DataHolder options) {
            super(false);
            currentFilePath = (Path) options.getAll().get(CrosspageLinkExtension.CURRENT_FILE_PATH);
            PagesStructure optpagesStructure =  (PagesStructure) options.getAll().get(CrosspageLinkExtension.PAGES_STRUCTURE);
            if (optpagesStructure==null){
                pagesStructure = new DefaultFileIndexer.DefaultPagesStructure(List.of());
            }else {
                pagesStructure = (PagesStructure) options.getAll().get(CrosspageLinkExtension.PAGES_STRUCTURE);
            }
            addNodes(Link.class);
        }

        private Set<Path> toPaths(List<? extends Page> pages) {
            Set<Path> paths = new HashSet<>();
            for (Page page: pages){
                paths.add(page.path().toAbsolutePath());
                paths.addAll(toPaths(page.children()));
            }
            return paths;
        }

        @NotNull
        @Override
        public NodePostProcessor apply(@NotNull Document document) {
            return new CrosspageLinkPostProcessor(
                    currentFilePath,
                    toPaths(pagesStructure.pages()));
        }
    }
}
