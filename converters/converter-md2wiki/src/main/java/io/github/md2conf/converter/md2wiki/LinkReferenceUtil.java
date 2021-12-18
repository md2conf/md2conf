package io.github.md2conf.converter.md2wiki;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.LinkNodeBase;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class LinkReferenceUtil {

    public static Set<String> findLocalLinks(Node document) {
        Set<String> result = new HashSet<>();
        result.addAll(collectImageUrls(document));
//        result.addAll(collectLinkUrls(document)); //todo add support for file links
        return result;
    }

    @NotNull
    private static Set<String> collectImageUrls(Node document) {
        UrlCollectorVisitor<Image> imageUrlCollectorVisitor = new UrlCollectorVisitor<>();
        VisitHandler<Image> imageVisitHandler = new VisitHandler<>(Image.class, imageUrlCollectorVisitor);
        NodeVisitor visitor = new NodeVisitor(imageVisitHandler);
        visitor.visit(document);
        return imageUrlCollectorVisitor.getUrls();
    }

//    @NotNull
//    private static Set<String> collectLinkUrls(Node document) {
//        UrlCollectorVisitor<Link> urlCollectorVisitor = new UrlCollectorVisitor<>();
//        VisitHandler<Link> linkVisitHandler = new VisitHandler<>(Link.class, urlCollectorVisitor);
//        NodeVisitor visitor = new NodeVisitor(linkVisitHandler);
//        visitor.visit(document);
//        return urlCollectorVisitor.getUrls();
//    }

    static class UrlCollectorVisitor<T extends LinkNodeBase> implements Visitor<T> {

        private final Set<String> urls;

        UrlCollectorVisitor() {
            urls = new HashSet<>();
        }

        @Override
        public void visit(@NotNull T node) {
            String url = node.getUrl().toStringOrNull();
            if (url != null) {
                urls.add(url);
            }
        }

        public Set<String> getUrls() {
            return urls;
        }
    }
}
