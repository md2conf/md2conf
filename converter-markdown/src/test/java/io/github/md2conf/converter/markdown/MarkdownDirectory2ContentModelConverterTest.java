package io.github.md2conf.converter.markdown;

import io.github.md2conf.model.ConfluenceContent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

class MarkdownDirectory2ContentModelConverterTest {


    @Test
    void simpleTest() {

        MarkdownDirectory2ContentModelConverter converter = new MarkdownDirectory2ContentModelConverter();
        Path documentationRootFolder = Paths.get("src/test/resources/markdown-based-pages-structure");
        ConfluenceContent confluenceContent = converter.convert(documentationRootFolder);
        Assertions.assertThat(confluenceContent).isNotNull();



    }
}