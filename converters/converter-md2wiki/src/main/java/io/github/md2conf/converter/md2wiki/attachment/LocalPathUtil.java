package io.github.md2conf.converter.md2wiki.attachment;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;
import io.github.md2conf.flexmart.ext.local.attachments.LocalAttachmentLink;
import io.github.md2conf.flexmart.ext.local.image.LocalImage;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalPathUtil {

    public static List<Path> collectLocalImagePaths(Node document) {
        PathCollectorVisitor<LocalImage> pathCollectorVisitor = new PathCollectorVisitor<>();
        VisitHandler<LocalImage> visitHandler = new VisitHandler<>(LocalImage.class, pathCollectorVisitor);
        NodeVisitor visitor = new NodeVisitor(visitHandler);
        visitor.visit(document);
        return new ArrayList<>(pathCollectorVisitor.getPaths());
    }

    public static List<Path> collectLocalAttachmentPaths(Node document) {
        PathCollectorVisitor<LocalAttachmentLink> pathCollectorVisitor = new PathCollectorVisitor<>();
        VisitHandler<LocalAttachmentLink> visitHandler = new VisitHandler<>(LocalAttachmentLink.class, pathCollectorVisitor);
        NodeVisitor visitor = new NodeVisitor(visitHandler);
        visitor.visit(document);
        return new ArrayList<>(pathCollectorVisitor.getPaths());
    }

    public static class PathCollectorVisitor<N extends Node> implements Visitor<N> {
        private final Set<Path> paths;

        public PathCollectorVisitor() {
            paths = new HashSet<>();
        }

        @Override
        public void visit(@NotNull N node) {
            Path path = null;
            if (node instanceof LocalAttachmentLink) {
                path = ((LocalAttachmentLink) node).getPath();
            }
            if (node instanceof LocalImage) {
                path = ((LocalImage) node).getPath();
            }
            if (path != null) {
                paths.add(path);
            }
        }

        public Set<Path> getPaths() {
            return paths;
        }
    }
}
