package io.github.md2conf.converter.md2wiki;

import io.github.md2conf.converter.ConfluencePageFactory;
import io.github.md2conf.converter.ExtractTitleStrategy;
import io.github.md2conf.indexer.DefaultFileIndexer;
import io.github.md2conf.indexer.FileIndexer;
import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.github.md2conf.indexer.PathNameUtils.ATTACHMENTS_SUFFIX;
import static org.assertj.core.api.Assertions.assertThat;

class Md2WikiConverterTest {

    @TempDir
    private Path outputPath;

    @Test
    void convert_markdown_page_tree() throws IOException {
        Md2WikiConverter copyingConverter = new Md2WikiConverter(new ConfluencePageFactory(ExtractTitleStrategy.FROM_FILENAME), outputPath);
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DefaultFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_page_tree"));
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = copyingConverter.convert(pagesStructure);
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
    void convert_markdown_page_tree_with_inline_local_image() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(new ConfluencePageFactory(ExtractTitleStrategy.FROM_FILENAME), outputPath);
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DefaultFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_with_inline_images"));
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(1);
        assertThat(outputPath).isNotEmptyDirectory();
        assertThat(outputPath).isDirectoryContaining("glob:**/index.wiki");
        String dirWithAttachments = "index"+ATTACHMENTS_SUFFIX;
        assertThat(outputPath.resolve(dirWithAttachments)).isDirectory().exists();
        assertThat(outputPath.resolve(dirWithAttachments)).isDirectoryContaining("glob:**/sample.gif");
    }

    @Test
    void convert_markdown_page_tree_with_local_attachment_link() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(new ConfluencePageFactory(ExtractTitleStrategy.FROM_FILENAME), outputPath);
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DefaultFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_with_local_attachment"));
        assertThat(pagesStructure.pages()).hasSize(1);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(1);
        assertThat(outputPath).isNotEmptyDirectory();
        assertThat(outputPath).isDirectoryContaining("glob:**/index.wiki");
        String dirWithAttachments = "index"+ATTACHMENTS_SUFFIX;
        assertThat(outputPath.resolve(dirWithAttachments)).isDirectory().exists();
        assertThat(outputPath.resolve(dirWithAttachments)).isDirectoryContaining("glob:**/sample.txt");
    }

    @Test
    void convert_markdown_crosslinks() throws IOException {
        Md2WikiConverter md2WikiConverter = new Md2WikiConverter(new ConfluencePageFactory(ExtractTitleStrategy.FROM_FIRST_HEADER), outputPath);
        var prop = new FileIndexerConfigurationProperties();
        prop.setFileExtension("md");
        FileIndexer fileIndexer = new DefaultFileIndexer(prop);
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/markdown_crosslinks"));
        assertThat(pagesStructure.pages()).hasSize(2);
        ConfluenceContentModel model = md2WikiConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(2);
        assertThat(outputPath).isNotEmptyDirectory();
        assertThat(outputPath).isDirectoryContaining("glob:**/a.wiki");
        assertThat(outputPath).isDirectoryContaining("glob:**/b.wiki");
        assertThat(outputPath.resolve("a.wiki")).content().contains("Page B");
        assertThat(outputPath.resolve("b.wiki")).content().contains("Page A");

    }

}