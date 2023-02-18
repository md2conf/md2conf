package io.github.md2conf.title.processor;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Block;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.TextCollectingVisitor;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

public class MarkdownTitleExtractor implements TitleExtractor {

    private final Parser PARSER = Parser.builder().build();

    public String extractTitle(Path path) throws IOException {
        String markdown = null;
            markdown = FileUtils.readFileToString(path.toFile(), Charset.defaultCharset());
        Node document = PARSER.parse(markdown);
        String res =  findTitle(document);
        if (res==null){
            throw new IllegalArgumentException("Cannot extract title from markdown file at path " + path);
        }
        return res;
    }

    private static String findTitle(Node root) {
        if (root instanceof Heading) {
            Heading h = (Heading) root;
            if ((h.getLevel() == 1|| h.getLevel() == 2 || h.getLevel() == 3) && h.hasChildren()) {
                TextCollectingVisitor collectingVisitor = new TextCollectingVisitor();
                return collectingVisitor.collectAndGetText(h);
            }
        }

        if (root instanceof Block && root.hasChildren()) {
            Node child = root.getFirstChild();
            while (child != null) {
                String title = findTitle(child);
                if (title != null) {
                    return title;
                }
                child = child.getNext();
            }
        }

        return null;
    }
}
