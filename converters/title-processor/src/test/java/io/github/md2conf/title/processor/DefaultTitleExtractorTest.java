package io.github.md2conf.title.processor;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTitleExtractorTest {

    private final DefaultTitleExtractor fileNameTitleExtractor = new DefaultTitleExtractor(TitleExtractStrategy.FROM_FILENAME);
    private final DefaultTitleExtractor firstHeaderTitleExtractor = new DefaultTitleExtractor(TitleExtractStrategy.FROM_FIRST_HEADER);

    @Test
    void fileNametitleExtractor() throws IOException {
        String path = "src/test/resources/first_header_h1.wiki";
        File file = new File(path);
        String title = firstHeaderTitleExtractor.extractTitle(file.toPath());
        assertThat(title).isEqualTo("My header");
    }

    @Test
    void extract_title_from_filename() throws IOException {
        String path = "src/test/resources/first_header_h1.wiki";
        File file = new File(path);
        String title = fileNameTitleExtractor.extractTitle(file.toPath());
        assertThat(title).isEqualTo("first_header_h1");
    }
}