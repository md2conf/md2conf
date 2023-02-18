package io.github.md2conf.title.processor;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class FilenameTitleExtractorTest {

    private final TitleExtractor filenameTitleExtractor = new FilenameTitleExtractor();

    @Test
    void extract_title_from_filename() throws IOException {
        String path = "src/test/resources/s p a c e s.and_long.extension";
        File file = new File(path);
        String title = filenameTitleExtractor.extractTitle(file.toPath());
        assertThat(title).isEqualTo("s p a c e s.and_long");
    }
}