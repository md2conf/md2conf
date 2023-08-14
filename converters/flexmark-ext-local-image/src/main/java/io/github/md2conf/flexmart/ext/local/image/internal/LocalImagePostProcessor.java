package io.github.md2conf.flexmart.ext.local.image.internal;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeTracker;
import com.vladsch.flexmark.util.data.DataHolder;
import io.github.md2conf.flexmart.ext.local.image.LocalImage;
import io.github.md2conf.flexmart.ext.local.image.LocalImageExtension;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

public class LocalImagePostProcessor extends NodePostProcessor {

    private final Path currentFilePath;

    public LocalImagePostProcessor(Path currentFilePath) {
        this.currentFilePath = currentFilePath;
    }

    public void process(@NotNull NodeTracker state, @NotNull Node node) {
        if (node instanceof Image) {
            String url = ((Image) node).getUrl().toString();
            if (url.startsWith("http://") || url.startsWith("https://")) {
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

            Node parent = node.getParent();
            Node prev = node.getPrevious();
            Node next = node.getNext();
            LocalImage localImage = new LocalImage((Image) node);
            localImage.setPath(resolvedPath);
            localImage.setFileName(resolvedPath.getFileName().toString());
            localImage.takeChildren(node);
            node.unlink();
            if (parent != null) {
                if (prev!=null){
                    prev.insertAfter(localImage);
                }
                else if (next!=null){
                    next.insertBefore(localImage);
                }
                else {
                    parent.appendChild(localImage);
                }
            }
            state.nodeRemoved(node);
            state.nodeAddedWithChildren(localImage);
        }
    }

    public static boolean isRegularFileExists(Path path) {
        return Files.exists(path) && Files.isRegularFile(path);
    }

    public static class Factory extends NodePostProcessorFactory {
        private final Path currentFilePath;

        public Factory(DataHolder options) {
            super(false);
            currentFilePath = (Path) options.getAll().get(LocalImageExtension.CURRENT_FILE_PATH);
            addNodes(Image.class);
        }

        @NotNull
        @Override
        public NodePostProcessor apply(@NotNull Document document) {
            return new LocalImagePostProcessor(currentFilePath);
        }
    }
}
