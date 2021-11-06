package io.github.md2conf.indexer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class TitleExtractorTest {

    @Test
    void extract_title_from_filename() throws IOException {
        String path = "src/test/resources/s p a c e s.and_long.extension";
        File file = new File(path);
        String title = TitleExtractor.extractTitle(file.toPath(), ExtractTitleStrategy.FROM_FILENAME);
        Assertions.assertThat(title).isEqualTo("s p a c e s.and_long");
    }

    @Test
    void extract_title_from_wiki_content_h1() throws IOException {
        String path = "src/test/resources/first_header_h1.wiki";
        File file = new File(path);
        String title = TitleExtractor.extractTitle(file.toPath(), ExtractTitleStrategy.FROM_FIRST_HEADER);
        Assertions.assertThat(title).isEqualTo("My header");
    }

    @Test
    void extract_title_from_wiki_content_h3() throws IOException {
        String path = "src/test/resources/first_header_h3.wiki";
        File file = new File(path);
        String title = TitleExtractor.extractTitle(file.toPath(), ExtractTitleStrategy.FROM_FIRST_HEADER);
        Assertions.assertThat(title).isEqualTo("another big header");
    }

    @Test
    void extract_title_from_wiki_content_without_headers() throws IOException {
        String path = "src/test/resources/just_text.wiki";
        File file = new File(path);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () -> TitleExtractor.extractTitle(file.toPath(), ExtractTitleStrategy.FROM_FIRST_HEADER));
    }
}