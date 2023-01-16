package io.github.md2conf.converter.md2wiki.link;

import com.vladsch.flexmark.ast.InlineLinkNode;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;

import java.util.Set;

public class InlineLinkUrlUtil {

    public static <T extends InlineLinkNode> Set<String> collectUrlsOfNodeType(Node document, Class<T> tClass) {
        UrlCollectorVisitor<T> collectorVisitor = new UrlCollectorVisitor<>();
        VisitHandler<T> visitHandler = new VisitHandler<>(tClass, collectorVisitor);
        NodeVisitor visitor = new NodeVisitor(visitHandler);
        visitor.visit(document);
        return collectorVisitor.getUrls();
    }

}
