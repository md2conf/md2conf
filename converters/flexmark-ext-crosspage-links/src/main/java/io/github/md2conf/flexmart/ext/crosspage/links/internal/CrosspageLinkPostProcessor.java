package io.github.md2conf.flexmart.ext.crosspage.links.internal;

import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeTracker;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.misc.Utils;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import io.github.md2conf.flexmart.ext.crosspage.links.CrosspageLink;
import io.github.md2conf.flexmart.ext.crosspage.links.CrosspageLinkExtension;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class CrosspageLinkPostProcessor extends NodePostProcessor {
    private final Path currentFilePath;
    private final Map<Path,String> titleMap;

    public CrosspageLinkPostProcessor(Path currentFilePath,
                                      Map<Path,String> titleMap) {
        this.currentFilePath = currentFilePath;
        this.titleMap = titleMap;
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
            Path relative = currentFilePath.resolve(Utils.urlDecode(url, "UTF-8"));
            Path absolute = Path.of(url);
            final Path resolvedPath;
            if (isRegularFileExists(relative)) {
                resolvedPath = relative;
            } else if (isRegularFileExists(absolute)) {
                resolvedPath = absolute;
            } else {
                return;
            }
            if (titleMap.containsKey(resolvedPath.toAbsolutePath())){
                Node parent = node.getParent();
                CrosspageLink crosspageLink = new CrosspageLink((Link) node);
                crosspageLink.setTitle(BasedSequence.of(titleMap.get(resolvedPath.toAbsolutePath())));
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
        private final Map<Path,String> titleMap;

        @SuppressWarnings("unchecked")
        public Factory(DataHolder options) {
            super(false);
            currentFilePath = (Path) options.getAll().get(CrosspageLinkExtension.CURRENT_FILE_PATH);
            this.titleMap = (Map<Path, String>) options.getAll().get(CrosspageLinkExtension.TITLE_MAP);
            addNodes(Link.class);
        }

        @NotNull
        @Override
        public NodePostProcessor apply(@NotNull Document document) {
            return new CrosspageLinkPostProcessor(
                    currentFilePath,
                    titleMap);
        }
    }
}
