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

public class ImageUrlCollector {

    public static Set<String> collectImageUrls(Node document) {
        UrlCollectorVisitor<Image> imageUrlCollectorVisitor = new UrlCollectorVisitor<>();
        VisitHandler<Image> imageVisitHandler = new VisitHandler<>(Image.class, imageUrlCollectorVisitor);
        NodeVisitor visitor = new NodeVisitor(imageVisitHandler);
        visitor.visit(document);
        return imageUrlCollectorVisitor.getUrls();
    }

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
