package io.github.md2conf.indexer.impl;

import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import io.github.md2conf.indexer.OrphanFileStrategy;
import io.github.md2conf.indexer.PagesStructure;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static io.github.md2conf.indexer.FileIndexerConfigurationPropertiesFactory.aDefaultIndexerConfigurationProperties;
import static org.assertj.core.api.Assertions.assertThat;


class ChildInSubDirectoryFileIndexerTest extends AbstractFileIndexerTest {


    ChildInSubDirectoryFileIndexerTest() {
        super(new ChildInSubDirectoryFileIndexer(new FileIndexerConfigurationProperties()));
    }


    @Test
    void index_dir_with_xml_files() {
        ChildInSubDirectoryFileIndexer defaultIndexer = new ChildInSubDirectoryFileIndexer(aDefaultIndexerConfigurationProperties()
                .withFileExtension("xml")
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
        ChildInSubDirectoryFileIndexer defaultIndexer = new ChildInSubDirectoryFileIndexer(aDefaultIndexerConfigurationProperties()
                .withFileExtension("wiki")
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
        ChildInSubDirectoryFileIndexer markdownIndexer = new ChildInSubDirectoryFileIndexer(markdownProps);
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
        ChildInSubDirectoryFileIndexer markdownIndexer = new ChildInSubDirectoryFileIndexer(markdownProps);
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

    @Test
    void dir_with_index_md_and_orphans_ignore() {
        FileIndexerConfigurationProperties markdownProps = new FileIndexerConfigurationProperties();
        markdownProps.setFileExtension("md");
        markdownProps.setRootPage(null);
        ChildInSubDirectoryFileIndexer markdownIndexer = new ChildInSubDirectoryFileIndexer(markdownProps);
        String path = "src/test/resources/dir_with_index_md_and_orphans";
        File f = new File(path);
        PagesStructure structure = markdownIndexer.indexPath(f.toPath());
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isNotEmpty();
        assertThat(structure.pages()).hasSize(1);
        assertThat(structure.pages().get(0).children()).isEmpty();
    }

    @Test
    void dir_with_index_md_and_orphans_add_to_top_level() {
        FileIndexerConfigurationProperties markdownProps = new FileIndexerConfigurationProperties();
        markdownProps.setFileExtension("md");
        markdownProps.setRootPage(null);
        markdownProps.setOrhanPagesStrategy(OrphanFileStrategy.ADD_TO_TOP_LEVEL_PAGES);
        ChildInSubDirectoryFileIndexer markdownIndexer = new ChildInSubDirectoryFileIndexer(markdownProps);
        String path = "src/test/resources/dir_with_index_md_and_orphans";
        File f = new File(path);
        PagesStructure structure = markdownIndexer.indexPath(f.toPath());
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).hasSize(5)
                .extracting(v -> v.path().getFileName().toString())
                .containsExactlyInAnyOrder("index.md", "index.md", "orhan.md", "child_level_2.md", "level_2.md");
        assertThat(structure.pages().get(0).children()).isEmpty();
    }
}