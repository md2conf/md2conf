package io.github.md2conf.indexer.impl;

import io.github.md2conf.indexer.ChildLayout;
import io.github.md2conf.indexer.FileIndexer;
import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import io.github.md2conf.indexer.OrphanFileAction;
import io.github.md2conf.indexer.Page;
import io.github.md2conf.indexer.PagesStructure;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static io.github.md2conf.indexer.FileIndexerConfigurationPropertiesFactory.aDefaultIndexerConfigurationProperties;
import static org.assertj.core.api.Assertions.assertThat;

class ChildInSameDirectoryFileIndexerTest extends AbstractFileIndexerTest{

    public  ChildInSameDirectoryFileIndexerTest() {
        super(new ChildInSubDirectoryFileIndexer(new FileIndexerConfigurationProperties()));
    }

    @Test
    void index_dir_with_xml_files() {
        FileIndexer defaultIndexer = new ChildInSameDirectoryFileIndexer(aDefaultIndexerConfigurationProperties()
                .fileExtension("xml")
                .excludePattern("glob:**")
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
        assertThat(structure.pages().get(0).children()).filteredOn(page -> page.path().endsWith("index.md")).singleElement().matches(v -> v.attachments().size() == 1);
    }

    @Test
    void index_dir_with_skipupdate() {
        FileIndexer markdownIndexer = mdFileIndexer();
        String path = "src/test/resources/dir_with_skipupdatefile";
        File f = new File(path);
        PagesStructure structure = markdownIndexer.indexPath(f.toPath());
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isNotEmpty();
        assertThat(structure.pages()).hasSize(1);
        assertThat(structure.pages().get(0).children()).hasSize(3);
        assertThat(structure.pages().get(0).children()).filteredOn(page -> page.path().endsWith("page_a.md")).singleElement()
                .matches(v -> v.children().isEmpty(), "empty children")
                .matches(Page::skipUpdate, "skip update");
        assertThat(structure.pages().get(0).children()).filteredOn(page -> page.path().endsWith("page_b.md")).singleElement()
                .matches(v -> v.children().isEmpty(), "empty children")
                .matches(v->!v.skipUpdate(), "not skip update");
        assertThat(structure.pages().get(0).children()).filteredOn(page -> page.path().endsWith("index.md")).singleElement().matches(v -> v.children().size() == 2);
        assertThat(structure.pages().get(0).children()).filteredOn(page -> page.path().endsWith("index.md")).singleElement().matches(v -> v.attachments().size() == 1);
    }

    @Test
    void dir_with_index_md_and_orphans_ignore() {
        FileIndexer markdownIndexer = mdFileIndexer(OrphanFileAction.IGNORE);
        String path = "src/test/resources/dir_with_index_md_and_orphans";
        File f = new File(path);
        PagesStructure structure = markdownIndexer.indexPath(f.toPath());
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isNotEmpty();
        assertThat(structure.pages()).hasSize(1);
        assertThat(structure.pages().get(0).children()).hasSize(1);
        assertThat(structure.pages().get(0).children()).hasSize(1).singleElement().matches(page -> page.path().endsWith("child/index.md"));
        assertThat(structure.pages().get(0).children().get(0).children()).hasSize(1).singleElement().matches(page -> page.path().endsWith("child/child_level_2.md"));
    }

    @Test
    void dir_with_index_md_and_orphans_add_to_top_level() {
        FileIndexer markdownIndexer = mdFileIndexer(OrphanFileAction.ADD_TO_TOP_LEVEL_PAGES);
        String path = "src/test/resources/dir_with_index_md_and_orphans";
        File f = new File(path);
        PagesStructure structure = markdownIndexer.indexPath(f.toPath());
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isNotEmpty();
        assertThat(structure.pages()).hasSize(3)
                .extracting(v -> v.path().getFileName().toString())
                .containsExactlyInAnyOrder("index.md", "orhan.md", "level_2.md");
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

    private static FileIndexer mdFileIndexer(OrphanFileAction orphanFileAction) {
        FileIndexerConfigurationProperties markdownProps = new FileIndexerConfigurationProperties();
        markdownProps.setFileExtension("md");
        markdownProps.setRootPage(null);
        markdownProps.setChildLayout(ChildLayout.SAME_DIRECTORY);
            markdownProps.setOrphanFileAction(orphanFileAction);
        return new ChildInSameDirectoryFileIndexer(markdownProps);
    }
}