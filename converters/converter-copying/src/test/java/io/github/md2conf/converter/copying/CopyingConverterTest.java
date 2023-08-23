package io.github.md2conf.converter.copying;

import io.github.md2conf.indexer.ChildInSubDirectoryFileIndexer;
import io.github.md2conf.indexer.FileIndexer;
import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;
import io.github.md2conf.title.processor.DefaultPageStructureTitleProcessor;
import io.github.md2conf.title.processor.PageStructureTitleProcessor;
import io.github.md2conf.title.processor.TitleExtractStrategy;
import org.assertj.core.api.Assertions;
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

    Condition<ConfluencePage> page_with_attachments = new Condition<ConfluencePage>(s -> !s.getAttachments().isEmpty(), "a page must have attachments", "" );

    private final PageStructureTitleProcessor titleProcessor = new DefaultPageStructureTitleProcessor(TitleExtractStrategy.FROM_FILENAME, null, null, false);


    @Test
    void copy_empty_dir() throws IOException {
        CopyingConverter copyingConverter = new CopyingConverter(titleProcessor, outputPath, false);
        FileIndexer fileIndexer = new ChildInSubDirectoryFileIndexer(new FileIndexerConfigurationProperties());
        PagesStructure pagesStructure = fileIndexer.indexPath(emptyDir);
        ConfluenceContentModel model = copyingConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).isEmpty();
    }

    @Test
    void copy_example_page_tree() throws IOException {
        CopyingConverter copyingConverter = new CopyingConverter(titleProcessor, outputPath, false);
        FileIndexer fileIndexer = new ChildInSubDirectoryFileIndexer(new FileIndexerConfigurationProperties());
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/example_page_tree"));
        assertThat(pagesStructure.pages()).hasSize(2);
        ConfluenceContentModel model = copyingConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(2);
        assertPageTreeResult();
    }

    private void assertPageTreeResult() {
        assertThat(outputPath).isNotEmptyDirectory();
        assertThat(outputPath).isDirectoryContaining("glob:**/parent.wiki");
        assertThat(outputPath.resolve("parent")).isDirectoryContaining("glob:**/child-1.wiki");
        assertThat(outputPath).isDirectoryContaining("glob:**/page-a.wiki");
        assertThat(outputPath).isDirectoryNotContaining("glob:**/child-1");
    }

    @Test
    void copy_example_page_tree_twice() throws IOException {
        CopyingConverter copyingConverter = new CopyingConverter(titleProcessor, outputPath, false);
        FileIndexer fileIndexer = new ChildInSubDirectoryFileIndexer(new FileIndexerConfigurationProperties());
        PagesStructure pagesStructure = fileIndexer.indexPath(Paths.get("src/test/resources/example_page_tree"));
        assertThat(pagesStructure.pages()).hasSize(2);
        ConfluenceContentModel model = copyingConverter.convert(pagesStructure);
        model = copyingConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(2);
        assertPageTreeResult();
    }

    @Test
    void copy_example_page_tree_with_title_remove() throws IOException {
        CopyingConverter copyingConverter = new CopyingConverter(titleProcessor, outputPath, true);
        FileIndexer fileIndexer = new ChildInSubDirectoryFileIndexer(new FileIndexerConfigurationProperties());
        Path input = Paths.get("src/test/resources/example_page_tree");
        PagesStructure pagesStructure = fileIndexer.indexPath(input);
        Assertions.assertThat(input.resolve("parent.wiki")).isRegularFile().content().contains("h1. parent");
        assertThat(pagesStructure.pages()).hasSize(2);
        ConfluenceContentModel model = copyingConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).hasSize(2);
        assertThat(outputPath).isNotEmptyDirectory();
        assertThat(outputPath).isDirectoryContaining("glob:**/parent.wiki");
        Assertions.assertThat(outputPath.resolve("parent.wiki")).isRegularFile().content().doesNotContain("h1. parent");

    }

    @Test
    void attachments_are_copied() throws IOException {
        CopyingConverter copyingConverter = new CopyingConverter(titleProcessor, outputPath, false);
        FileIndexer fileIndexer = new ChildInSubDirectoryFileIndexer(new FileIndexerConfigurationProperties());
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