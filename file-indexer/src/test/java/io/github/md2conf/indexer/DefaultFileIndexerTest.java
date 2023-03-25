package io.github.md2conf.indexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static io.github.md2conf.indexer.IndexerConfigurationPropertiesFactory.aDefaultIndexerConfigurationProperties;
import static org.assertj.core.api.Assertions.assertThat;


class DefaultFileIndexerTest {

    @TempDir
    private Path tmpDir;

    private final DefaultFileIndexer defaultIndexer = new DefaultFileIndexer(new FileIndexerConfigurationProperties());

    @Test
    void index_empty_dir() {
        PagesStructure structure = defaultIndexer.indexPath(tmpDir);
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isEmpty();
    }

    @Test
    void index_dir_with_hidden_files() {
        String path = "src/test/resources/dir_with_hidden_files";
        File f = new File(path);
        PagesStructure structure = defaultIndexer.indexPath(f.toPath());
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isEmpty();
    }

    @Test
    void index_dir_with_attachments() {
        String path = "src/test/resources/dir_with_attachments";
        File f = new File(path);
        PagesStructure structure = defaultIndexer.indexPath(f.toPath());
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isNotEmpty();
        assertThat(structure.pages()).hasSize(1);
        assertThat(structure.pages().get(0)).isNotNull();
        assertThat(structure.pages().get(0).children()).isNotEmpty();
        assertThat(structure.pages().get(0).attachments()).isNotEmpty();
        assertThat(structure.pages().get(0).attachments()).hasSize(2);
    }

    @Test
    void index_dir_with_xml_files() {
        DefaultFileIndexer defaultIndexer = new DefaultFileIndexer(aDefaultIndexerConfigurationProperties()
                .withFileExtension("xml")
                .withIncludePattern("glob:**")
                .build());
        String path = "src/test/resources/dir_with_xml_files";
        Path rootDir = (new File(path)).toPath();
        assertThat(rootDir).isDirectory();
        assertThat(rootDir).isDirectoryContaining("glob:**.xml");

        PagesStructure structure = defaultIndexer.indexPath(rootDir);
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isNotEmpty();
        assertThat(structure.pages()).hasSize(1);
        assertThat(structure.pages().get(0).children()).hasSize(1);
        assertThat(structure.pages().get(0).children().get(0).children()).hasSize(1);
        assertThat(structure.pages().get(0).children().get(0).children().get(0).children()).isEmpty();
    }

    @Test
    void test_dir_with_name_collision() {
        DefaultFileIndexer defaultIndexer = new DefaultFileIndexer(aDefaultIndexerConfigurationProperties()
                .withFileExtension("wiki")
                .withIncludePattern("glob:**")
                .build());
        String path = "src/test/resources/dir_with_name_collision";
        Path rootDir = (new File(path)).toPath();
        assertThat(rootDir).isDirectory();
        assertThat(rootDir).isDirectoryContaining("glob:**.wiki");

        PagesStructure model = defaultIndexer.indexPath(rootDir);
        assertThat(model).isNotNull();
        assertThat(model.pages()).isNotEmpty();
        assertThat(model.pages()).hasSize(1);
        assertThat(model.pages().get(0).children()).hasSize(1);
        assertThat(model.pages().get(0).children().get(0).children()).isEmpty();
    }

    @Test
    void index_dir_with_dir_with_several_pages_and_no_root_path() {
        FileIndexerConfigurationProperties markdownProps = new FileIndexerConfigurationProperties();
        markdownProps.setFileExtension("md");
        markdownProps.setRootPage(null);
        DefaultFileIndexer markdownIndexer = new DefaultFileIndexer(markdownProps);
        String path = "src/test/resources/dir_with_several_pages";
        File f = new File(path);
        PagesStructure structure = markdownIndexer.indexPath(f.toPath());
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isNotEmpty();
        assertThat(structure.pages()).hasSize(3);
        assertThat(structure.pages()).filteredOn(page -> page.path().endsWith("page_a.md")).singleElement().matches(v -> v.children().size() == 2);
        assertThat(structure.pages()).filteredOn(page -> page.path().endsWith("page_b.md")).singleElement().matches(v -> v.children().isEmpty());
        assertThat(structure.pages()).filteredOn(page -> page.path().endsWith("index.md")).hasSize(1);
    }

    @Test
    void index_dir_with_dir_with_several_pages_and_root_path_specified() {
        FileIndexerConfigurationProperties markdownProps = new FileIndexerConfigurationProperties();
        markdownProps.setFileExtension("md");
        markdownProps.setRootPage("index.md");
        DefaultFileIndexer markdownIndexer = new DefaultFileIndexer(markdownProps);
        String path = "src/test/resources/dir_with_several_pages";
        File f = new File(path);
        PagesStructure structure = markdownIndexer.indexPath(f.toPath());
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isNotEmpty();
        assertThat(structure.pages()).hasSize(1);
        assertThat(structure.pages()).filteredOn(page -> page.path().endsWith("page_a.md")).isEmpty();
        assertThat(structure.pages()).filteredOn(page -> page.path().endsWith("page_b.md")).isEmpty();
        assertThat(structure.pages()).filteredOn(page -> page.path().endsWith("index.md")).hasSize(1);
        assertThat(structure.pages().get(0).children()).filteredOn(page -> page.path().endsWith("page_a.md")).singleElement().matches(v -> v.children().size() == 2);
        assertThat(structure.pages().get(0).children()).filteredOn(page -> page.path().endsWith("page_b.md")).singleElement().matches(v -> v.children().isEmpty());

    }
}