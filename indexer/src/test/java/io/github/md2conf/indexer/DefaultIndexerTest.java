package io.github.md2conf.indexer;

import io.github.md2conf.model.ConfluenceContentModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static io.github.md2conf.indexer.IndexerConfigurationPropertiesFactory.aTitleFromFilenameIndexerConfigurationProperties;

class DefaultIndexerTest {

    @TempDir
    private Path tmpDir;

    private DefaultIndexer defaultIndexer = new DefaultIndexer(new IndexerConfigurationProperties());

    @Test
    void index_empty_dir() {
        ConfluenceContentModel model = defaultIndexer.indexPath(tmpDir);
        Assertions.assertThat(model).isNotNull();
        Assertions.assertThat(model.getPages()).isEmpty();
    }

    @Test
    void index_dir_with_hidden_files() {
        String path = "src/test/resources/dir_with_hidden_files";
        File f = new File(path);
        ConfluenceContentModel model = defaultIndexer.indexPath(f.toPath());
        Assertions.assertThat(model).isNotNull();
        Assertions.assertThat(model.getPages()).isEmpty();
    }

    @Test
    void index_dir_with_xml_files() {
        DefaultIndexer defaultIndexer = new DefaultIndexer(aTitleFromFilenameIndexerConfigurationProperties()
                .withFileExtension("xml")
                .build());
        String path = "src/test/resources/dir_with_xml_files";
        Path rootDir = (new File(path)).toPath();
        Assertions.assertThat(rootDir).isDirectory();
        Assertions.assertThat(rootDir).isDirectoryContaining("glob:**.xml");

        ConfluenceContentModel model = defaultIndexer.indexPath(rootDir);
        Assertions.assertThat(model).isNotNull();
        Assertions.assertThat(model.getPages()).isNotEmpty();
        Assertions.assertThat(model.getPages()).hasSize(1);
        Assertions.assertThat(model.getPages().get(0).getChildren()).hasSize(1);
        Assertions.assertThat(model.getPages().get(0).getChildren().get(0).getChildren()).hasSize(1);
        Assertions.assertThat(model.getPages().get(0).getChildren().get(0).getChildren().get(0).getChildren()).isEmpty();
    }

    @Test
    void test_dir_with_name_collision() {
        DefaultIndexer defaultIndexer = new DefaultIndexer(aTitleFromFilenameIndexerConfigurationProperties()
                .withFileExtension("wiki")
                .build());
        String path = "src/test/resources/dir_with_name_collision";
        Path rootDir = (new File(path)).toPath();
        Assertions.assertThat(rootDir).isDirectory();
        Assertions.assertThat(rootDir).isDirectoryContaining("glob:**.wiki");

        ConfluenceContentModel model = defaultIndexer.indexPath(rootDir);
        Assertions.assertThat(model).isNotNull();
        Assertions.assertThat(model.getPages()).isNotEmpty();
        Assertions.assertThat(model.getPages()).hasSize(1);
        Assertions.assertThat(model.getPages().get(0).getTitle()).isEqualTo("1");
        Assertions.assertThat(model.getPages().get(0).getChildren()).hasSize(1);
        Assertions.assertThat(model.getPages().get(0).getChildren().get(0).getTitle()).isEqualTo("2");
        Assertions.assertThat(model.getPages().get(0).getChildren().get(0).getChildren()).isEmpty();
    }
}