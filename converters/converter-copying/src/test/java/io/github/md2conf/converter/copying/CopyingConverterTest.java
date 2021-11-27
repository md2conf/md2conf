package io.github.md2conf.converter.copying;

import io.github.md2conf.converter.ConfluencePageFactory;
import io.github.md2conf.converter.ExtractTitleStrategy;
import io.github.md2conf.indexer.DefaultFileIndexer;
import io.github.md2conf.indexer.FileIndexer;
import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class CopyingConverterTest {

    @TempDir
    private Path emptyDir;

    @TempDir
    private Path outputPath;

    Condition<ConfluencePage> page_with_attachments = new Condition<ConfluencePage>(s -> !s.getAttachments().isEmpty(), "a page must have attchaments", "" );


    @Test
    void copy_empty_dir() throws IOException {
        CopyingConverter copyingConverter = new CopyingConverter(new ConfluencePageFactory(ExtractTitleStrategy.FROM_FILENAME), outputPath);
        FileIndexer fileIndexer = new DefaultFileIndexer(new FileIndexerConfigurationProperties());
        PagesStructure pagesStructure = fileIndexer.indexPath(emptyDir);
        ConfluenceContentModel model = copyingConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).isEmpty();
    }

    @Test
    void copy_example_page_tree() throws IOException {
        CopyingConverter copyingConverter = new CopyingConverter(new ConfluencePageFactory(ExtractTitleStrategy.FROM_FILENAME), outputPath);
        FileIndexer fileIndexer = new DefaultFileIndexer(new FileIndexerConfigurationProperties());
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/example_page_tree"));
        assertThat(pagesStructure.pages()).hasSize(2);
        ConfluenceContentModel model = copyingConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(2);
        assertThat(outputPath).isNotEmptyDirectory();
        assertThat(outputPath).isDirectoryContaining("glob:**/parent.wiki");
        assertThat(outputPath.resolve("parent")).isDirectoryContaining("glob:**/child-1.wiki");
        assertThat(outputPath).isDirectoryContaining("glob:**/page-a.wiki");
        assertThat(outputPath).isDirectoryNotContaining("glob:**/child-1");
    }

    @Test
    void attachments_are_copied() throws IOException {
        CopyingConverter copyingConverter = new CopyingConverter(new ConfluencePageFactory(ExtractTitleStrategy.FROM_FILENAME), outputPath);
        FileIndexer fileIndexer = new DefaultFileIndexer(new FileIndexerConfigurationProperties());
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/example_page_tree"));
        assertThat(pagesStructure.pages()).hasSize(2);
        ConfluenceContentModel model = copyingConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(2);
        assertThat(model.getPages()).haveExactly(1,page_with_attachments);
        assertThat(outputPath).isNotEmptyDirectory();
        assertThat(outputPath.resolve("page-a_attachments")).isDirectoryContaining("glob:**/attach.txt");
    }
}