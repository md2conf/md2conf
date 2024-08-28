package io.github.md2conf.indexer.ignore;

import io.github.md2conf.indexer.DefaultPage;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class SkipUpdateMarkerTest {

    @Test
    void testNoSkipUpdateFile() {
        String path = "src/test/resources/dir_with_xml_files";
        Path rootDir = (new File(path)).toPath();
        SkipUpdateMarker skipUpdateMarker = new SkipUpdateMarker(rootDir);
        DefaultPage page = new DefaultPage(Path.of(path));
        skipUpdateMarker.markIfNecessary(page);
        assertThat(page.skipUpdate()).isFalse();
    }

    @Test
    void skipupdateRuleWithWildcard() {
        String path = "src/test/resources/dir_with_skipupdatefile";
        Path rootDir = (new File(path)).toPath();
        SkipUpdateMarker skipUpdateMarker = new SkipUpdateMarker(rootDir);
        DefaultPage page = new DefaultPage(Path.of("src/test/resources/dir_with_skipupdatefile/page_a.md"));
        skipUpdateMarker.markIfNecessary(page);
        assertThat(page.skipUpdate()).isTrue();
    }

    @Test
    void skipupdateRuleSubPath() {
        String path = "src/test/resources/dir_with_skipupdatefile";
        Path rootDir = (new File(path)).toPath();
        SkipUpdateMarker skipUpdateMarker = new SkipUpdateMarker(rootDir);
        DefaultPage page = new DefaultPage(Path.of("src/test/resources/dir_with_skipupdatefile/page_a/another_page.md"));
        skipUpdateMarker.markIfNecessary(page);
        assertThat(page.skipUpdate()).isTrue();
    }
}