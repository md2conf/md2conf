package io.github.md2conf.converter.md2wiki.attachment;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ImageUrlUtil {

    public static Set<String> collectUrlsOfImages(Node document) {
        ImageUrlCollectorVisitor collectorVisitor = new ImageUrlCollectorVisitor();
        VisitHandler<Image> visitHandler = new VisitHandler<>(Image.class, collectorVisitor);
        NodeVisitor visitor = new NodeVisitor(visitHandler);
        visitor.visit(document);
        return collectorVisitor.getUrls();
    }


    static class ImageUrlCollectorVisitor implements Visitor<Image> {
        private final Set<String> urls;

        ImageUrlCollectorVisitor() {
            urls = new HashSet<>();
        }

        @Override
        public void visit(@NotNull Image node) {
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
