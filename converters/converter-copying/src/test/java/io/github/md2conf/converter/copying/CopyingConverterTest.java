package io.github.md2conf.converter.copying;

import io.github.md2conf.converter.ConfluencePageFactory;
import io.github.md2conf.converter.ExtractTitleStrategy;
import io.github.md2conf.indexer.DefaultIndexer;
import io.github.md2conf.indexer.Indexer;
import io.github.md2conf.indexer.IndexerConfigurationProperties;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
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


    @Test
    void copy_empty_dir() throws IOException {
        CopyingConverter copyingConverter = new CopyingConverter(new ConfluencePageFactory(ExtractTitleStrategy.FROM_FILENAME), outputPath);
        Indexer indexer = new DefaultIndexer(new IndexerConfigurationProperties());
        PagesStructure pagesStructure = indexer.indexPath(emptyDir);
        ConfluenceContentModel model = copyingConverter.convert(pagesStructure);
        assertThat(model).isNotNull();
        assertThat(model.getPages()).isEmpty();
    }

    @Test
    void copy_example_page_tree() throws IOException {
        CopyingConverter copyingConverter = new CopyingConverter(new ConfluencePageFactory(ExtractTitleStrategy.FROM_FILENAME), outputPath);
        Indexer indexer = new DefaultIndexer(new IndexerConfigurationProperties());
        PagesStructure pagesStructure = indexer.indexPath(Paths.get("src/test/resources/example_page_tree"));
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
}