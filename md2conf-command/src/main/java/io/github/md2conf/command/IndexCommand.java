package io.github.md2conf.command;

import io.github.md2conf.indexer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.file.Path;

import static picocli.CommandLine.Option.NULL_VALUE;

@CommandLine.Command(name = "index", description = "Index input directory to build page structure and print results")
public class IndexCommand implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(IndexCommand.class);

    @CommandLine.ArgGroup(multiplicity = "1", heading = "Indexer options:\n", order = 1)
    private IndexerOptions indexerOptions;

    @Override
    public void run() {
        PagesStructure pagesStructure = IndexCommand.indexInputDirectory(indexerOptions);
    }

    public static PagesStructure indexInputDirectory(IndexerOptions indexerOptions) {
        logger.info("Indexing path {}", indexerOptions.inputDirectory);
        logger.info("Child relation established using {} layout", indexerOptions.indexerChildLayout);
        FileIndexerConfigurationProperties fileIndexerConfigurationProperties = createFileIndexerConfigurationProperties(indexerOptions);
        FileIndexer fileIndexer = new DelegatingFileIndexer(fileIndexerConfigurationProperties);
        PagesStructure pagesStructure = fileIndexer.indexPath(indexerOptions.inputDirectory);
        if (pagesStructure.pages().isEmpty()) {
            logger.warn("No files found in input directory. Used file indexer options {}",
                    fileIndexerConfigurationProperties);
        } else {
            PagesStructurePrinter printer = new PagesStructurePrinter();
            printer.prettyPrint(pagesStructure);
        }
        return pagesStructure;
    }

    private static FileIndexerConfigurationProperties createFileIndexerConfigurationProperties(IndexerOptions indexerOptions) {
        return FileIndexerConfigurationProperties.builder()
                .fileExtension(indexerOptions.indexerFileExtension)
                .excludePattern(indexerOptions.indexerExcludePattern)
                .rootPage(indexerOptions.indexerRootPage)
                .childLayout(indexerOptions.indexerChildLayout)
                .orphanFileAction(indexerOptions.indexerOrphanFileAction)
                .build();
    }


    public static class IndexerOptions {
        @CommandLine.Option(names = {"-i", "--input-dir", "--indexer-input-dir"}, required = true, description = "Input directory")
        public Path inputDirectory;
        @CommandLine.Option(names = {"--indexer-file-extension"}, description = "File extension to index as confluence content pages", defaultValue = "md")
        public String indexerFileExtension = "md";
        @CommandLine.Option(names = {"--indexer-exclude-pattern"}, description = "Exclude pattern in format of glob:** or regexp:.*. For syntax see javadoc of java.nio.file.FileSystem.getPathMatcher method", defaultValue = "glob:**/.*")
        public String indexerExcludePattern = "glob:**/.*";
        @CommandLine.Option(names = {"--indexer-root-page"}, description = "Use specified page as parent page for all another top-level pages in an input directory", defaultValue = NULL_VALUE)
        public String indexerRootPage = null;
        @CommandLine.Option(names = {"--indexer-child-layout"}, description =
                "SUB_DIRECTORY is layout when source files for children pages resides in directory with the name equals to basename of parent file\n " +
                        "SAME_DIRECTORY is layout when file with name 'index.md' or 'README.md' is the source file of parent page and other files in the directory are source files for children pages",
                defaultValue = "SUB_DIRECTORY",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        public ChildLayout indexerChildLayout = ChildLayout.SUB_DIRECTORY;
        @CommandLine.Option(names = {"--indexer-orphan-file-action"},
                description = "What to do with page which source file that are not top-level page and not child of any page. Valid values: ${COMPLETION-CANDIDATES}",
                defaultValue = "IGNORE",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        public OrphanFileAction indexerOrphanFileAction = OrphanFileAction.IGNORE;
    }
}
