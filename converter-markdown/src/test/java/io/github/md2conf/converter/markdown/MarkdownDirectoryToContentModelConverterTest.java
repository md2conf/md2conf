package io.github.md2conf.converter.markdown;

import java.nio.file.Path;
import java.nio.file.Paths;

class MarkdownDirectoryToContentModelConverterTest {


    //@Test todo
    void simpleTest() {
        MarkdownConverterConfigurationProperties properties =new MarkdownConverterConfigurationProperties();
        MarkdownContentModelProducer converter = new MarkdownContentModelProducer();
        Path documentationRootFolder = Paths.get("src/test/resources/markdown-based-pages-structure");
        converter.produce(properties);
     //   Assertions.assertThat(confluenceContent).isNotNull();



    }
}