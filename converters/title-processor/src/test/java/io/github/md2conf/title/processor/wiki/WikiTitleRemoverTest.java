package io.github.md2conf.title.processor.wiki;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class WikiTitleRemoverTest {

    @TempDir
    Path tmpPath;

    @Test
    void remove_title_from_content_at_path() throws IOException {
        String path = "src/test/resources/first_header_h1.wiki";
        File file = new File(path);
        Path copied = Path.of(tmpPath.toString()).resolve(path);
        FileUtils.copyFile(file, copied.toFile());
        assertThat(copied).content().contains("h1. My header");
        WikiHeaderRemover.removeFirstHeader(copied);
        assertThat(copied).content().doesNotContain("h1. My header");
        assertThat(copied).content().contains("h2. another header");
    }

    @Test
    void remove_title_from_content_without_title_at_path() throws IOException {
        String path = "src/test/resources/just_text.wiki";
        File file = new File(path);
        Path copied = Path.of(tmpPath.toString()).resolve(path);
        FileUtils.copyFile(file, copied.toFile());
        assertThat(copied).content().contains("text");
        WikiHeaderRemover.removeFirstHeader(copied);
        assertThat(copied).content().contains("text");
    }

}