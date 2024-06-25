package io.github.md2conf.markdown.formatter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MarkdownFormatterTest {

    @Test
    void testFormat() {
        String text = "header1\n=======";
        String res = MarkdownFormatter.format(text).trim();
        Assertions.assertThat(res).isEqualTo(text);
    }


    @Test
    void testFormatLink() {
        String text = "header1\n=======\n\n" +
                "[What is Confluence?](/pages/viewpage.action?pageId=65552)";
        String res = MarkdownFormatter.format(text).trim();
        Assertions.assertThat(res).isEqualTo(text);
    }

    @Test
    void reformatLongLines() {

        String markdown = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        String res = MarkdownFormatter.format(markdown).trim();
        Assertions.assertThat(res).isEqualTo("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna\n" +
                "aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis\n" +
                "aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint\n" +
                "occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

    }
}