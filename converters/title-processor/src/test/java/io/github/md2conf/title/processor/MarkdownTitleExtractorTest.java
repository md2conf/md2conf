package io.github.md2conf.title.processor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownTitleExtractorTest {

    private final MarkdownTitleExtractor markdownTitleExtractor = new MarkdownTitleExtractor();

    @Test
    void extract_title_first_header_l1() throws IOException {
        String path = "src/test/resources/first_header_l1.md";
        File file = new File(path);
        String title = markdownTitleExtractor.extractTitle(file.toPath());
        assertThat(title).isEqualTo("My header");
    }

    @Test
    void extract_title_first_header_l1_alt() throws IOException {
        String path = "src/test/resources/first_header_l1_alt.md";
        File file = new File(path);
        String title = markdownTitleExtractor.extractTitle(file.toPath());
        assertThat(title).isEqualTo("Header 1");
    }

    @Test
    void extract_title_first_header_l2() throws IOException {
        String path = "src/test/resources/first_header_l2.md";
        File file = new File(path);
        String title = markdownTitleExtractor.extractTitle(file.toPath());
        assertThat(title).isEqualTo("another header");
    }

    @Test
    void extract_title_first_header_l2_alt() throws IOException {
        String path = "src/test/resources/first_header_l2_alt.md";
        File file = new File(path);
        String title = markdownTitleExtractor.extractTitle(file.toPath());
        assertThat(title).isEqualTo("Header 2");
    }

    @Test
    void extract_title_first_header_l3() throws IOException {
        String path = "src/test/resources/first_header_l3.md";
        File file = new File(path);
        String title = markdownTitleExtractor.extractTitle(file.toPath());
        assertThat(title).isEqualTo("another big header");
    }

    @Test
    void extract_title_first_header_l4_fail() {
        String path = "src/test/resources/first_header_l4.md";
        File file = new File(path);
        Assertions.assertThatThrownBy(()->markdownTitleExtractor.extractTitle(file.toPath()))
                .hasMessage("Cannot extract title from markdown file at path " + path);
    }
}