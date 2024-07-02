package io.github.md2conf.markdown.formatter;

import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import java.nio.file.Path;
import java.util.Map;

public class CrosspageLinkReplacer {

    NodeVisitor visitor = new NodeVisitor(
            new VisitHandler<>(Link.class, this::visit)
    );

    private final Map<Long,Path> pageIdPathMap;
    private final Path currentDir;

    public CrosspageLinkReplacer(Map<Long,Path> pageIdPathMap, Path currentDir) {
        this.pageIdPathMap = pageIdPathMap;
        this.currentDir = currentDir.toAbsolutePath();
    }

    public void replacePageLinks(Node node) {
        visitor.visit(node);
    }

    private void visit(Link node) {
        if (node.getPageRef().startsWith("/pages/viewpage.action?pageId=")) {
            Long pageId = extractPageId(node.getPageRef().toString());
            if (pageIdPathMap.containsKey(pageId)) {
                Path pagePath = pageIdPathMap.get(pageId).toAbsolutePath();
                Path relativePath = currentDir.relativize(pagePath);
                BasedSequence url = BasedSequence.of(relativePath.toString());
                node.setUrl(url);
                node.setUrlChars(url);
            }
        }
    }

    private static Long extractPageId(String url) {
        String[] parts = url.split("=");
        long res = 0L;
        if (parts.length >= 2) {
            try {
                res = Long.parseLong(parts[1]);
            }
            catch (NumberFormatException e) {
                //no op
            }
        }
        return res;
    }

}
