package io.github.md2conf.title.processor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static io.github.md2conf.title.processor.WikiTitleExtractor.isConfluenceWikiHeaderLine;
import static org.assertj.core.api.Assertions.assertThat;

class WikiTitleExtractorTest {

    private WikiTitleExtractor titleExtractor = new WikiTitleExtractor();

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

    @Test
    void extract_title_from_wiki_content_h1() throws IOException {
        String path = "src/test/resources/first_header_h1.wiki";
        File file = new File(path);
        String title = titleExtractor.extractTitle(file.toPath());
        assertThat(title).isEqualTo("My header");
    }

    @Test
    void extract_title_from_wiki_content_h3() throws IOException {
        String path = "src/test/resources/first_header_h3.wiki";
        File file = new File(path);
        String title = titleExtractor.extractTitle(file.toPath());
        assertThat(title).isEqualTo("another big header");
    }

    @Test
    void extract_title_from_wiki_content_without_headers() throws IOException {
        String path = "src/test/resources/just_text.wiki";
        File file = new File(path);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () -> titleExtractor.extractTitle(file.toPath()));
    }

}