package io.github.md2conf.title.processor;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FirstHeaderTitleExtractorTest {

    private FirstHeaderTitleExtractor firstHeaderTitleExtractor  =new FirstHeaderTitleExtractor();

    @Test
    void extract_title_from_wiki() throws IOException {
        String path = "src/test/resources/first_header_h1.wiki";
        File file = new File(path);
        String title = firstHeaderTitleExtractor.extractTitle(file.toPath());
        assertThat(title).isEqualTo("My header");
    }

    @Test
    void extract_title_from_md() throws IOException {
        String path = "src/test/resources/first_header_l1.md";
        File file = new File(path);
        String title = firstHeaderTitleExtractor.extractTitle(file.toPath());
        assertThat(title).isEqualTo("My header");
    }

    @Test
    void extract_title_from_unknown() throws IOException {
        String path = "src/test/resources/sample.txt";
        File file = new File(path);
        assertThatThrownBy (()->firstHeaderTitleExtractor.extractTitle(file.toPath()))
                .hasMessage("FirstHeaderTitleExtractor for file extension \"txt\" is not implemented");
    }

}