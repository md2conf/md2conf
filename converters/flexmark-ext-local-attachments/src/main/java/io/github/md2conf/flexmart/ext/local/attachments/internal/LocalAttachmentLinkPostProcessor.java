package io.github.md2conf.flexmart.ext.local.attachments.internal;

import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeTracker;
import com.vladsch.flexmark.util.data.DataHolder;
import io.github.md2conf.flexmart.ext.local.attachments.LocalAttachmentLink;
import io.github.md2conf.flexmart.ext.local.attachments.LocalAttachmentLinkExtension;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocalAttachmentLinkPostProcessor extends NodePostProcessor {

    private final Path currentFilePath;

    public LocalAttachmentLinkPostProcessor(Path currentFilePath) {
        this.currentFilePath = currentFilePath;
    }

    public void process(@NotNull NodeTracker state, @NotNull Node node) {
        if (node instanceof Link) {
            String url = ((Link) node).getUrl().toString();
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return;
            }
            if (url.endsWith(".md")) {
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
            LocalAttachmentLink attachmentLink = new LocalAttachmentLink((Link) node);
            attachmentLink.setPath(resolvedPath);
            attachmentLink.takeChildren(node);
            node.unlink();
            if (parent != null) {
                parent.appendChild(attachmentLink);
            }
            state.nodeRemoved(node);
            state.nodeAddedWithChildren(attachmentLink);
        }
    }

    public static boolean isRegularFileExists(Path path) {
        return Files.exists(path) && Files.isRegularFile(path);
    }

    public static class Factory extends NodePostProcessorFactory {
        private final Path currentFilePath;

        public Factory(DataHolder options) {
            super(false);
            currentFilePath = (Path) options.getAll().get(LocalAttachmentLinkExtension.CURRENT_FILE_PATH);
            addNodes(Link.class);
        }

        @NotNull
        @Override
        public NodePostProcessor apply(@NotNull Document document) {
            return new LocalAttachmentLinkPostProcessor(currentFilePath);
        }
    }
}
