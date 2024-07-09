package io.github.md2conf.markdown.formatter;

import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.format.options.HeadingStyle;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MarkdownFormatterTest {

    private final MarkdownFormatter formatter = new MarkdownFormatter();

    @Test
    void testFormat() {
        String text = "header1\n=======";
        String res = formatter.format(text).trim();
        Assertions.assertThat(res).isEqualTo(text);
    }


    @Test
    void testFormatLink() {
        String text = "header1\n=======\n\n" +
                "[What is Confluence?](/pages/viewpage.action?pageId=65552)";
        String res = formatter.format(text).trim();
        Assertions.assertThat(res).isEqualTo(text);
    }

    @Test
    void testFormatHeading() {
        MutableDataSet mutableDataSet = new MutableDataSet();
        mutableDataSet.set(Formatter.HEADING_STYLE, HeadingStyle.ATX_PREFERRED);
        MarkdownFormatter markdownFormatter = new MarkdownFormatter(mutableDataSet);
        String text = "header1\n=======\n\n" +
                "some text";
        String res = markdownFormatter.format(text).trim();
        Assertions.assertThat(res).isEqualTo("# header1\n" +
                "\n" +
                "some text");
    }

    @Test
    void reformatLongLines() {
        String markdown = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        String res = formatter.format(markdown).trim();
        Assertions.assertThat(res).isEqualTo("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna\n" +
                "aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis\n" +
                "aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint\n" +
                "occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
    }

    @Test
    void reformatLongLinesWithCustomMargin() {
        MutableDataSet mutableDataSet = new MutableDataSet();
        mutableDataSet.set(Formatter.RIGHT_MARGIN, 5);
        MarkdownFormatter markdownFormatter = new MarkdownFormatter(mutableDataSet);
        String markdown = "Lorem ipsum dolor sit amet";
        String res = markdownFormatter.format(markdown).trim();
        Assertions.assertThat(res).isEqualTo("Lorem\n" +
                "ipsum\n" +
                "dolor\n" +
                "sit\n" +
                "amet");
    }
}