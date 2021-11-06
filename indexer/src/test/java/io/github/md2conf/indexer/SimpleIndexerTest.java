package io.github.md2conf.indexer;

import io.github.md2conf.model.ConfluenceContentModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static io.github.md2conf.indexer.IndexerConfigurationPropertiesFactory.aTitleFromFilenameIndexerConfigurationProperties;

class SimpleIndexerTest {

    private SimpleIndexer simpleIndexer = new SimpleIndexer(new IndexerConfigurationProperties());

    @Test
    void index_empty_dir() {
        String path = "src/test/resources/empty-dir";
        File f = new File(path);
        ConfluenceContentModel model = simpleIndexer.indexPath(f.toPath());
        Assertions.assertThat(model).isNotNull();
        Assertions.assertThat(model.getPages()).isEmpty();
    }

    @Test
    void index_dir_with_hidden_files() {
        String path = "src/test/resources/dir_with_hidden_files";
        File f = new File(path);
        ConfluenceContentModel model = simpleIndexer.indexPath(f.toPath());
        Assertions.assertThat(model).isNotNull();
        Assertions.assertThat(model.getPages()).isEmpty();
    }

    @Test
    void index_dir_with_xml_files() {
        SimpleIndexer simpleIndexer = new SimpleIndexer(aTitleFromFilenameIndexerConfigurationProperties()
                .withFileExtension("xml")
                .build());
        String path = "src/test/resources/dir_with_xml_files";
        Path rootDir = (new File(path)).toPath();
        Assertions.assertThat(rootDir).isDirectory();
        Assertions.assertThat(rootDir).isDirectoryContaining("glob:**.xml");

        ConfluenceContentModel model = simpleIndexer.indexPath(rootDir);
        Assertions.assertThat(model).isNotNull();
        Assertions.assertThat(model.getPages()).isNotEmpty();

    }
}