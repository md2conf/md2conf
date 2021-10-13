package io.github.md2conf.converter.markdown;

import io.github.md2conf.model.ConfluenceContentModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

class MarkdownMainConverterTest {

    @TempDir
    Path outputTmpDir;

    @Test
    void saving_to_output_works() {

        MarkdownConverterConfigurationProperties properties = new MarkdownConverterConfigurationProperties();
        properties.setInputDirectory("src/test/resources/markdown-based-pages-structure");
        properties.setOutputDirectory(outputTmpDir.toAbsolutePath().toString());
        MarkdownMainConverter converter = new MarkdownMainConverter();

        converter.convertAndSave(properties);

        Assertions.assertThat(outputTmpDir).isNotEmptyDirectory();
        Assertions.assertThat(outputTmpDir).isDirectoryContaining(path -> path.getFileName().toString().equals(ConfluenceContentModel.DEFAULT_FILE_NAME));

    }
}