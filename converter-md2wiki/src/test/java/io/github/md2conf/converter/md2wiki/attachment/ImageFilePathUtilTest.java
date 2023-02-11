package io.github.md2conf.converter.md2wiki.attachment;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static io.github.md2conf.converter.md2wiki.attachment.ImageFilePathUtil.filterExistingPaths;
import static org.assertj.core.api.Assertions.assertThat;

class ImageFilePathUtilTest {

    @Test
    void test_http_link_not_filter() {
        assertThat(filterExistingPaths(Set.of("http://example.com"), Path.of(""))).isEmpty();
    }

    @Test
    void test_https_link_not_filter() {
        assertThat(filterExistingPaths(Set.of("https://example.com"), Path.of(""))).isEmpty();
    }

    @Test
    void test_non_existsent_file_not_filter() {
        assertThat(filterExistingPaths(Set.of("non_existent_file"), Path.of(""))).isEmpty();
    }

    @Test
    void test_absolute_path_filter() {
        Path path = Paths.get("src/test/resources/markdown_examples/local_image_relative_path.md");
        assertThat(filterExistingPaths(Set.of(path.toAbsolutePath().toString()), Path.of("")))
                .hasSize(1);
    }

    @Test
    void test_relative_path_filter() {
        assertThat(filterExistingPaths(Set.of("test/resources/markdown_examples/local_image_relative_path.md"), Path.of("src")))
                .hasSize(1);
    }
}