package io.github.md2conf.flexmart.ext.curly.braced.escaper;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.jira.converter.JiraConverterExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Verifies that curly and square braces inside inline code are encoded as HTML entities so that
 * Confluence does not interpret them as macro ({...}) or link ([...]) syntax.
 *
 * The CurlyBracedBlockExtension must be registered AFTER JiraConverterExtension for its Code
 * renderer to override JIRA's (the last-registered node renderer for a type wins).
 */
public class InlineCodeBracesTest {

    // same relative order as the real Md2WikiConverter pipeline: JIRA first, then the escaper
    private static final DataHolder OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS, List.of(
                    JiraConverterExtension.create(),
                    CurlyBracedBlockExtension.create()))
            .toImmutable();

    private static final DataHolder WITHOUT_ESCAPER = new MutableDataSet()
            .set(Parser.EXTENSIONS, List.of(JiraConverterExtension.create()))
            .toImmutable();

    private static String render(DataHolder options, String markdown) {
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parse(markdown);
        return renderer.render(document).trim();
    }

    @Test
    public void encodes_curly_braces() {
        assertEquals("{{&#123;toc&#125;}}", render(OPTIONS, "`{toc}`"));
    }

    @Test
    public void encodes_valid_looking_macro_as_literal_text() {
        assertEquals("{{&#123;status:colour=Green|title=On track|subtle=true&#125;}}",
                render(OPTIONS, "`{status:colour=Green|title=On track|subtle=true}`"));
    }

    @Test
    public void encodes_square_brackets() {
        assertEquals("{{&#91;x&#93;}}", render(OPTIONS, "`[x]`"));
        assertEquals("{{&#123; &#125; &#91; &#93;}}", render(OPTIONS, "`{ } [ ]`"));
    }

    @Test
    public void keeps_surrounding_text_and_encodes_braces() {
        assertEquals("a {{&#123;abc&#125;}} b", render(OPTIONS, "a `{abc}` b"));
    }

    @Test
    public void leaves_code_without_braces_unchanged() {
        assertEquals("{{plain}}", render(OPTIONS, "`plain`"));
    }

    @Test
    public void without_escaper_braces_leak_through() {
        // documents the dependency on the extension being registered after JIRA
        assertEquals("{{{toc}}}", render(WITHOUT_ESCAPER, "`{toc}`"));
    }
}
