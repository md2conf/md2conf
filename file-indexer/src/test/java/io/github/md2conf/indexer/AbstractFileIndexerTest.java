package io.github.md2conf.indexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractFileIndexerTest {

    @TempDir
    private Path tmpDir;

    protected final FileIndexer fileIndexer;

    AbstractFileIndexerTest(FileIndexer fileIndexer) {
        this.fileIndexer = fileIndexer;
    }

    @Test
    void index_empty_dir() {
        PagesStructure structure = fileIndexer.indexPath(tmpDir);
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isEmpty();
    }

    @Test
    void index_dir_with_hidden_files() {
        String path = "src/test/resources/dir_with_hidden_files";
        File f = new File(path);
        PagesStructure structure = fileIndexer.indexPath(f.toPath());
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isEmpty();
    }

    @Test
    void index_dir_with_attachments() {
        String path = "src/test/resources/dir_with_attachments";
        File f = new File(path);
        PagesStructure structure = fileIndexer.indexPath(f.toPath());
        assertThat(structure).isNotNull();
        assertThat(structure.pages()).isNotEmpty();
        assertThat(structure.pages()).hasSize(1);
        assertThat(structure.pages().get(0)).isNotNull();
        assertThat(structure.pages().get(0).children()).isNotEmpty();
        assertThat(structure.pages().get(0).attachments()).isNotEmpty();
        assertThat(structure.pages().get(0).attachments()).hasSize(2);
    }
}