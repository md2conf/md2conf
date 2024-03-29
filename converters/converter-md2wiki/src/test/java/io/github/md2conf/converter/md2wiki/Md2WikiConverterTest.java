package io.github.md2conf.converter.md2wiki;

import io.github.md2conf.indexer.DelegatingFileIndexer;
import io.github.md2conf.indexer.FileIndexer;
import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;
import io.github.md2conf.title.processor.DefaultPageStructureTitleProcessor;
import io.github.md2conf.title.processor.PageStructureTitleProcessor;
import io.github.md2conf.title.processor.TitleExtractStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.github.md2conf.indexer.PathNameUtils.ATTACHMENTS_SUFFIX;
import static org.assertj.core.api.Assertions.assertThat;

class Md2WikiConverterTest {

    private final PageStructureTitleProcessor titleProcessor = new DefaultPageStructureTitleProcessor(TitleExtractStrategy.FROM_FILENAME, null, null, false);
    private final PageStructureTitleProcessor titleProcessorFromFirstHeader = new DefaultPageStructureTitleProcessor(TitleExtractStrategy.FROM_FIRST_HEADER, null, null, false);


    @TempDir
    private Path outputPath;


    @Test
    void convert_markdown_single_page() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(titleProcessor, outputPath, false,false, "");
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DelegatingFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_with_inline_images"));
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(1);
        ConfluencePage confluencePage = model.getPages().get(0);
        assertThat(confluencePage.getType()).isEqualTo(ConfluenceContentModel.Type.WIKI);
        assertThat(confluencePage.getContentFilePath()).endsWith(".wiki");
        assertThat(confluencePage.getTitle()).isNotNull().isEqualTo("index");
        assertThat(outputPath).isDirectoryContaining("glob:**/index.wiki");
    }

    @Test
    void convert_markdown_page_tree() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(titleProcessor, outputPath, false,false, "");
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DelegatingFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_page_tree"));
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertModelOfPageTree(model);
    }

    private void assertModelOfPageTree(ConfluenceContentModel model) {
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(1);
        assertThat(model.getPages().get(0).getType()).isEqualTo(ConfluenceContentModel.Type.WIKI);
        assertThat(outputPath).isNotEmptyDirectory();
        assertThat(outputPath).isDirectoryContaining("glob:**/index.wiki");
        assertThat(outputPath.resolve("index")).isDirectoryContaining("glob:**/child-1.wiki");
        assertThat(outputPath.resolve("index")).isDirectoryContaining("glob:**/child-2.wiki");
        assertThat(outputPath.resolve("index").resolve("child-1")).isDirectoryContaining("glob:**/sub-child-1.wiki");
    }

    @Test
    void convert_markdown_page_tree_invoke_twice() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(titleProcessor, outputPath, false,false, "");
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DelegatingFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_page_tree"));
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        model = md2WikiConverter.convert(pagesStructure);
        assertModelOfPageTree(model);
    }

    @Test
    void convert_markdown_page_tree_heading_removed() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(titleProcessor, outputPath, true,false, "");
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DelegatingFileIndexer(prop);
        Path input = Paths.get("src/test/resources/markdown_page_tree");
        Assertions.assertThat(input.resolve("index.md")).isRegularFile().content().contains("Heading");
        PagesStructure pagesStructure = fileIndexer.indexPath(input);
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(1);
        assertThat(model.getPages().get(0).getType()).isEqualTo(ConfluenceContentModel.Type.WIKI);
        assertThat(outputPath.resolve("index.wiki")).isRegularFile().content().doesNotContain("Heading");
    }

    @Test
    void convert_markdown_page_tree_with_inline_local_image() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(titleProcessor, outputPath, false,false, "");
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DelegatingFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_with_inline_images"));
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(1);
        assertThat(outputPath).isDirectoryContaining("glob:**/index.wiki");
        String dirWithAttachments = "index"+ATTACHMENTS_SUFFIX;
        assertThat(outputPath.resolve(dirWithAttachments)).isDirectory().exists();
        assertThat(outputPath.resolve(dirWithAttachments)).isDirectoryContaining("glob:**/sample.gif");
    }

    @Test
    void convert_markdown_page_tree_with_local_attachment_link() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(titleProcessor, outputPath, false,false, "plantuml");
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DelegatingFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_with_local_attachment"));
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(1);
        assertThat(outputPath).isNotEmptyDirectory().isDirectoryContaining("glob:**/index.wiki");
        String dirWithAttachments = "index"+ATTACHMENTS_SUFFIX;
        assertThat(outputPath.resolve(dirWithAttachments)).isDirectory().exists();
        assertThat(outputPath.resolve(dirWithAttachments)).isDirectoryContaining("glob:**/sample.txt");
    }

    @Test
    void convert_markdown_crosslinks() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(titleProcessorFromFirstHeader, outputPath, false, false,"plantuml");
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DelegatingFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_crosslinks"));
        assertThat(pagesStructure.pages()).hasSize(2);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(2);
        assertThat(outputPath).isDirectoryContaining("glob:**/a.wiki");
        assertThat(outputPath).isDirectoryContaining("glob:**/b.wiki");
        assertThat(outputPath.resolve("a.wiki")).content().contains("Page B");
        assertThat(outputPath.resolve("b.wiki")).content().contains("Page A");
    }

    @Test
    void convert_markdown_plantuml_enabled() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(titleProcessorFromFirstHeader, outputPath, false, true,"plantuml");
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DelegatingFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_plantuml"));
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(1);
        assertThat(outputPath).isDirectoryContaining("glob:**/a.wiki");
        assertThat(outputPath.resolve("a.wiki")).isRegularFile().content().doesNotContain("{code}").contains("{plantuml}");
    }

    @Test
    void convert_markdown_plantumlrender_enabled() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(titleProcessorFromFirstHeader, outputPath, false, true,"plantumlrender");
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DelegatingFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_plantuml"));
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(1);
        assertThat(outputPath).isDirectoryContaining("glob:**/a.wiki");
        assertThat(outputPath.resolve("a.wiki")).isRegularFile().content().doesNotContain("{code}").contains("{plantumlrender}");
    }

    @Test
    void convert_markdown_plantuml_disabled() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(titleProcessorFromFirstHeader, outputPath, false, false,"plantuml");
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DelegatingFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_plantuml"));
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(1);
        assertThat(outputPath).isDirectoryContaining("glob:**/a.wiki");
        assertThat(outputPath.resolve("a.wiki")).isRegularFile().content().contains("{code}").doesNotContain("{plantuml}");
    }


    @Test
    void convert_codeblock() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(titleProcessorFromFirstHeader, outputPath, false, true,"plantuml");
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DelegatingFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_codeblocks"));
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(1);
        assertThat(outputPath).isDirectoryContaining("glob:**/codeblock.wiki");
        assertThat(outputPath.resolve("codeblock.wiki")).isRegularFile().content()
                .contains("{plantuml}")
                .contains("{code}")
                .contains("{code:language=java}");

    }

    @Test
    void convert_html_comment() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(titleProcessorFromFirstHeader, outputPath, false, true,"plantuml");
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DelegatingFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_html_comment"));
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(1);
        assertThat(outputPath).isDirectoryContaining("glob:**/page_with_html_comment.wiki");
        assertThat(outputPath.resolve("page_with_html_comment.wiki")).isRegularFile().content()
                .doesNotContain("{code:html}")
                .doesNotContain("HTML_COMMENT_BLOCK")
                .doesNotContain("INLINE_HTML_COMMENT");
    }

}