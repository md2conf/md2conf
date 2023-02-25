package io.github.md2conf.title.processor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.github.md2conf.title.processor.WikiTitleUtil.isConfluenceWikiHeaderLine;

class WikiTitleUtilTest {

    @Test
    void test_isConfluenceWikiHeaderLine() {
        Assertions.assertThat(isConfluenceWikiHeaderLine("h1. abc")).isTrue();
        Assertions.assertThat(isConfluenceWikiHeaderLine("h2. abc")).isTrue();
        Assertions.assertThat(isConfluenceWikiHeaderLine("h3. abc")).isTrue();
        Assertions.assertThat(isConfluenceWikiHeaderLine("   h3.")).isFalse();
        Assertions.assertThat(isConfluenceWikiHeaderLine("   h3. 1")).isTrue();
        Assertions.assertThat(isConfluenceWikiHeaderLine("h4. abc")).isFalse();
        Assertions.assertThat(isConfluenceWikiHeaderLine("abc")).isFalse();
    }


}