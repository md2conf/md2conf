package io.github.md2conf.markdown.formatter;

import com.vladsch.flexmark.util.ast.Node;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

class CrosspageLinkReplacerTest {

    private final MarkdownFormatter formatter = new MarkdownFormatter();

    @Test
    void replaceLink() {
        String text = "# Welcome to Confluence " +
                "\n\n" +
                "[What is Confluence?](/pages/viewpage.action?pageId=65552)";

        Node document = formatter.getParser().parse(text);
        CrosspageLinkReplacer visitor = new CrosspageLinkReplacer(Map.of(65552L, Path.of("./child/What is Confluence?.md")), Path.of("."));
        visitor.replacePageLinks(document);

        String res =  formatter.getRenderer().render(document);
        String expected = "# Welcome to Confluence" +   "\n\n" +"[What is Confluence?](child/What%20is%20Confluence?.md)\n";
        Assertions.assertThat(res).isEqualTo(expected);
    }
}