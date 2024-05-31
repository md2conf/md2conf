package io.github.md2conf.markdown.formatter;

import com.vladsch.flexmark.util.ast.Node;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static io.github.md2conf.markdown.formatter.MarkdownFormatter.PARSER;
import static io.github.md2conf.markdown.formatter.MarkdownFormatter.RENDERER;

class ImageAttachmentUrlReplacerTest {

    @Test
    void testImageAttachmentUrlIsReplaced() {
        String text = "#Welcome to Confluence" +
                "\n" +
                "![](/download/attachments/65551/welcome.png?version=1&modificationDate=1631164672537&api=v2)";

        Node document = PARSER.parse(text);
        ImageAttachmentUrlReplacer visitor = new ImageAttachmentUrlReplacer(List.of(Path.of("./Welcome to Confluence_attachments/welcome.png")));
        visitor.replaceUrl(document);

        String res =  RENDERER.render(document);
        String expected = "#Welcome to Confluence" +   "\n" +"![welcome.png](Welcome to Confluence_attachments/welcome.png)\n";
        Assertions.assertThat(res).isEqualTo(expected);
    }

    @Test
    void testImageAttachmentUrlWithEncodedSymbols() {
        String text = "#Welcome to Confluence" +
                "\n" +
                "![](/download/attachments/65551/welcome%20to%20Confluence.png?version=1&modificationDate=1631164672537&api=v2)";

        Node document = PARSER.parse(text);
        ImageAttachmentUrlReplacer visitor = new ImageAttachmentUrlReplacer(List.of(Path.of("./Welcome to Confluence_attachments/welcome to Confluence.png")));
        visitor.replaceUrl(document);

        String res =  RENDERER.render(document);
        String expected = "#Welcome to Confluence" +   "\n" +"![welcome to Confluence.png](Welcome to Confluence_attachments/welcome to Confluence.png)\n";
        Assertions.assertThat(res).isEqualTo(expected);
    }
}