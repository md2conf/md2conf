package io.github.md2conf.converter.md2wiki.attachment;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;
import io.github.md2conf.flexmart.ext.local.attachments.LocalAttachmentLink;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LocalAttachmentUtil {

    public static List<Path> collectLocalAttachmentPaths(Node document) {
        PathCollectorVisitor pathCollectorVisitor = new PathCollectorVisitor();
        VisitHandler<LocalAttachmentLink> visitHandler = new VisitHandler<>(LocalAttachmentLink.class, pathCollectorVisitor);
        NodeVisitor visitor = new NodeVisitor(visitHandler);
        visitor.visit(document);
        return new ArrayList<>(pathCollectorVisitor.getPaths());
    }

    public static class PathCollectorVisitor implements Visitor<LocalAttachmentLink> {
        private final Set<Path> paths;

        public PathCollectorVisitor() {
            paths = new HashSet<>();
        }

        @Override
        public void visit(@NotNull LocalAttachmentLink node) {
            if (node.getPath()!=null){
                paths.add(node.getPath());
            }
        }

        public Set<Path> getPaths() {
            return paths;
        }
    }
}
