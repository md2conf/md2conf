package io.github.md2conf.indexer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static io.github.md2conf.indexer.IndexerConfigurationPropertiesFactory.aDefaultIndexerConfigurationProperties;


class DefaultFileIndexerTest {

    @TempDir
    private Path tmpDir;

    private DefaultFileIndexer defaultIndexer = new DefaultFileIndexer(new FileIndexerConfigurationProperties());

    @Test
    void index_empty_dir() {
        PagesStructure structure = defaultIndexer.indexPath(tmpDir);
        Assertions.assertThat(structure).isNotNull();
        Assertions.assertThat(structure.pages()).isEmpty();
    }

    @Test
    void index_dir_with_hidden_files() {
        String path = "src/test/resources/dir_with_hidden_files";
        File f = new File(path);
        PagesStructure structure = defaultIndexer.indexPath(f.toPath());
        Assertions.assertThat(structure).isNotNull();
        Assertions.assertThat(structure.pages()).isEmpty();
    }

    @Test
    void index_dir_with_xml_files() {
        DefaultFileIndexer defaultIndexer = new DefaultFileIndexer(aDefaultIndexerConfigurationProperties()
                .withFileExtension("xml")
                .withIncludePattern("glob:**")
                .build());
        String path = "src/test/resources/dir_with_xml_files";
        Path rootDir = (new File(path)).toPath();
        Assertions.assertThat(rootDir).isDirectory();
        Assertions.assertThat(rootDir).isDirectoryContaining("glob:**.xml");

        PagesStructure structure  = defaultIndexer.indexPath(rootDir);
        Assertions.assertThat(structure).isNotNull();
        Assertions.assertThat(structure.pages()).isNotEmpty();
        Assertions.assertThat(structure.pages()).hasSize(1);
        Assertions.assertThat(structure.pages().get(0).children()).hasSize(1);
        Assertions.assertThat(structure.pages().get(0).children().get(0).children()).hasSize(1);
        Assertions.assertThat(structure.pages().get(0).children().get(0).children().get(0).children()).isEmpty();
    }

    @Test
    void test_dir_with_name_collision() {
        DefaultFileIndexer defaultIndexer = new DefaultFileIndexer(aDefaultIndexerConfigurationProperties()
                .withFileExtension("wiki")
                .withIncludePattern("glob:**")
                .build());
        String path = "src/test/resources/dir_with_name_collision";
        Path rootDir = (new File(path)).toPath();
        Assertions.assertThat(rootDir).isDirectory();
        Assertions.assertThat(rootDir).isDirectoryContaining("glob:**.wiki");

        PagesStructure model = defaultIndexer.indexPath(rootDir);
        Assertions.assertThat(model).isNotNull();
        Assertions.assertThat(model.pages()).isNotEmpty();
        Assertions.assertThat(model.pages()).hasSize(1);
        Assertions.assertThat(model.pages().get(0).children()).hasSize(1);
        Assertions.assertThat(model.pages().get(0).children().get(0).children()).isEmpty();
    }
}