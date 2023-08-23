package io.github.md2conf.indexer;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static io.github.md2conf.indexer.IndexerConfigurationPropertiesFactory.aDefaultIndexerConfigurationProperties;
import static org.assertj.core.api.Assertions.assertThat;

class ChildInSameDirectoryFileIndexerTest extends AbstractFileIndexerTest{

    public  ChildInSameDirectoryFileIndexerTest() {
        super(new ChildInSubDirectoryFileIndexer(new FileIndexerConfigurationProperties()));
    }

    @Test
    void index_dir_with_xml_files() {
        FileIndexer defaultIndexer = new ChildInSameDirectoryFileIndexer(aDefaultIndexerConfigurationProperties()
                .withFileExtension("xml")
                .withIncludePattern("glob:**")
                .build());
        String path = "src/test/resources/dir_with_xml_files";
        Path rootDir = (new File(path)).toPath();
        assertThat(rootDir).isDirectory().isDirectoryContaining("glob:**.xml");

        PagesStructure structure = defaultIndexer.indexPath(rootDir);
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isEmpty();
    }


    @Test
    void index_dir_with_several_pages() {
        FileIndexer markdownIndexer = mdFileIndexer();
        String path = "src/test/resources/dir_with_several_pages";
        File f = new File(path);
        PagesStructure structure = markdownIndexer.indexPath(f.toPath());
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isNotEmpty();
        assertThat(structure.pages()).hasSize(1);
        assertThat(structure.pages().get(0).children()).hasSize(3);
        assertThat(structure.pages().get(0).children()).filteredOn(page -> page.path().endsWith("page_a.md")).singleElement().matches(v -> v.children().size() == 0);
        assertThat(structure.pages().get(0).children()).filteredOn(page -> page.path().endsWith("page_b.md")).singleElement().matches(v -> v.children().size() == 0);
        assertThat(structure.pages().get(0).children()).filteredOn(page -> page.path().endsWith("index.md")).singleElement().matches(v -> v.children().size() == 1);
    }

    @Test
    void dir_with_index_md_and_orhans() {
        FileIndexer markdownIndexer = mdFileIndexer();
        String path = "src/test/resources/dir_with_index_md_and_orhans";
        File f = new File(path);
        PagesStructure structure = markdownIndexer.indexPath(f.toPath());
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isNotEmpty();
        assertThat(structure.pages()).hasSize(1);
        assertThat(structure.pages().get(0).children()).hasSize(1);
        assertThat(structure.pages().get(0).children()).hasSize(1).singleElement().matches(page -> page.path().endsWith("child/index.md"));
        assertThat(structure.pages().get(0).children().get(0).children()).hasSize(1).singleElement().matches(page -> page.path().endsWith("child/child_level_2.md"));
    }

    private static FileIndexer mdFileIndexer() {
        FileIndexerConfigurationProperties markdownProps = new FileIndexerConfigurationProperties();
        markdownProps.setFileExtension("md");
        markdownProps.setRootPage(null);
        markdownProps.setChildLayout(ChildLayout.SAME_DIRECTORY);
        return new ChildInSameDirectoryFileIndexer(markdownProps);
    }
}