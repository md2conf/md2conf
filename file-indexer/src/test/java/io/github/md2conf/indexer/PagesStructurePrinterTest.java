package io.github.md2conf.indexer;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

import static io.github.md2conf.indexer.IndexerConfigurationPropertiesFactory.aDefaultIndexerConfigurationProperties;
import static org.assertj.core.api.Assertions.assertThat;

class PagesStructurePrinterTest {

    private ListAppender<ILoggingEvent> logWatcher;

    @BeforeEach
    void setup() {
        logWatcher = new ListAppender<>();
        logWatcher.start();
        ((Logger) LoggerFactory.getLogger(PagesStructurePrinter.class)).addAppender(logWatcher);
    }

    @AfterEach
    void teardown() {
        ((Logger) LoggerFactory.getLogger(PagesStructurePrinter.class)).detachAndStopAllAppenders();
    }

    private final DefaultFileIndexer defaultIndexer = new DefaultFileIndexer(new FileIndexerConfigurationProperties());

    @Test
    void index_dir_with_attachments() {
        String path = "src/test/resources/dir_with_attachments";
        File f = new File(path);
        PagesStructure structure = defaultIndexer.indexPath(f.toPath());
        PagesStructurePrinter printer = new PagesStructurePrinter();
        printer.prettyPrint(structure);

        assertThat(logWatcher.list.get(0).getFormattedMessage()).contains("Page structure is:");
        assertThat(logWatcher.list.get(1).getFormattedMessage()).contains("└── 1.wiki");
        assertThat(logWatcher.list.get(2).getFormattedMessage()).contains("    └── 1/2.wiki");

    }

    @Test
    void test_dir_with_name_collision() {
        DefaultFileIndexer defaultIndexer = new DefaultFileIndexer(aDefaultIndexerConfigurationProperties()
                .withFileExtension("wiki")
                .withIncludePattern("glob:**")
                .build());
        String path = "src/test/resources/dir_with_name_collision";
        Path rootDir = (new File(path)).toPath();

        PagesStructure model = defaultIndexer.indexPath(rootDir);
        PagesStructurePrinter printer = new PagesStructurePrinter();
        printer.prettyPrint(model);

        assertThat(logWatcher.list.get(0).getFormattedMessage()).contains("Page structure is:");
        assertThat(logWatcher.list.get(1).getFormattedMessage()).contains("└── 1.wiki");
        assertThat(logWatcher.list.get(2).getFormattedMessage()).contains("    └── 1/2.wiki");
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
        PagesStructurePrinter printer = new PagesStructurePrinter();
        printer.prettyPrint(structure);

        assertThat(logWatcher.list.get(0).getFormattedMessage()).contains("Page structure is:");
        assertThat(logWatcher.list.get(1).getFormattedMessage()).contains("├── index.md");
        assertThat(logWatcher.list.get(2).getFormattedMessage()).contains("├── page_a.md");
        assertThat(logWatcher.list.get(3).getFormattedMessage()).contains("│   ├── page_a/sub_page_a.md");
        assertThat(logWatcher.list.get(4).getFormattedMessage()).contains("│   └── page_a/index.md");
        assertThat(logWatcher.list.get(5).getFormattedMessage()).contains("└── page_b.md");
    }

}