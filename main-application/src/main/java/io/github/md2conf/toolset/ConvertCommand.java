package io.github.md2conf.toolset;

import io.github.md2conf.converter.PageStructureConverter;
import io.github.md2conf.converter.copying.CopyingConverter;
import io.github.md2conf.converter.md2wiki.Md2WikiConverter;
import io.github.md2conf.converter.noop.NoopConverter;
import io.github.md2conf.converter.view2md.View2MdConverter;
import io.github.md2conf.indexer.ChildLayout;
import io.github.md2conf.indexer.DelegatingFileIndexer;
import io.github.md2conf.indexer.FileIndexer;
import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.indexer.PagesStructurePrinter;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.title.processor.DefaultPageStructureTitleProcessor;
import io.github.md2conf.title.processor.PageStructureTitleProcessor;
import io.github.md2conf.title.processor.TitleExtractStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static io.github.md2conf.model.util.ModelReadWriteUtil.saveConfluenceContentModelAtPath;
import static io.github.md2conf.toolset.ConvertCommand.ConverterType.MD2WIKI;
import static io.github.md2conf.toolset.ConvertCommand.ConverterType.VIEW2MD;
import static io.github.md2conf.toolset.PublishCommand.loadConfluenceContentModel;


@Command(name = "convert",
        description = "Convert files to `confluence-content-model` or from `confluence-content-model`")
public class ConvertCommand implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ConvertCommand.class);

    public final static String DEFAULT_OUTPUT = ".md2conf";

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1", heading = "Convert options:\n")
    private ConvertOptions convertOptions;

    @CommandLine.ArgGroup(exclusive = false, heading = "Indexer options:\n", order = 3)
    private IndexerOptions indexerOptions;

    //todo adjust description
    @CommandLine.Option(names = {"-m", "--confluence-content-model"}, description = "Path to file with `confluence-content-model` JSON file or to directory with confluence-content-model.json file. Default value is current working directory.", defaultValue = ".", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    public Path confluenceContentModelPath;

    @Override
    public void run() {
        var indexerOptionsLocal = indexerOptions == null ? new ConvertCommand.IndexerOptions() : indexerOptions;
        convert(this.convertOptions, indexerOptionsLocal, this.confluenceContentModelPath);
    }

    public static File convert(ConvertOptions convertOptions, IndexerOptions indexerOptions, Path confluenceContentModelPath) {
        initOptionsIfRequired(convertOptions);
        if (convertOptions.converter == VIEW2MD) {
            ConfluenceContentModel model = loadContentModelFromPathOrDefault(convertOptions, confluenceContentModelPath);
            View2MdConverter view2MdConverter = new View2MdConverter(convertOptions.outputDirectory);
            view2MdConverter.convert(model);
            logger.info("Converting to markdown result saved to {}", convertOptions.outputDirectory);
            return null;
        } else {
            PagesStructure pagesStructure = indexInputDirectory(convertOptions.inputDirectory, indexerOptions);
            PageStructureConverter converterService = createConverter(convertOptions);
            ConfluenceContentModel model = convert(pagesStructure, converterService);
            File contentModelFile = saveConfluenceContentModelAtPath(model, convertOptions.outputDirectory);
            logger.info("Confluence content model saved at file {}", contentModelFile);
            return contentModelFile;
        }
    }

    private static ConfluenceContentModel loadContentModelFromPathOrDefault(ConvertOptions convertOptions, Path confluenceContentModelPath) {
        ConfluenceContentModel model;
        if (confluenceContentModelPath == null || confluenceContentModelPath.toString().equals(".")) {
            model = loadConfluenceContentModel(convertOptions.inputDirectory);
        } else {
            model = loadConfluenceContentModel(confluenceContentModelPath);
        }
        return model;
    }


    protected static void initOptionsIfRequired(ConvertOptions convertOptions) {
        if (convertOptions.outputDirectory == null) {
            logger.warn("Output directory is not specified, default is " + DEFAULT_OUTPUT);
            convertOptions.outputDirectory = new File(convertOptions.inputDirectory.toFile(), DEFAULT_OUTPUT).toPath();
        }
    }

    protected static PagesStructure indexInputDirectory(Path inputDirectory, IndexerOptions indexerOptions) {
        logger.info("Indexing path {}", inputDirectory);
        logger.info("Child relation established using {} layout", indexerOptions.childLayout);
        FileIndexerConfigurationProperties fileIndexerConfigurationProperties = new FileIndexerConfigurationProperties();
        fileIndexerConfigurationProperties.setFileExtension(indexerOptions.fileExtension);
        fileIndexerConfigurationProperties.setExcludePattern(indexerOptions.excludePattern);
        fileIndexerConfigurationProperties.setRootPage(indexerOptions.indexerRootPage);
        fileIndexerConfigurationProperties.setChildLayout(indexerOptions.childLayout);
        FileIndexer fileIndexer = new DelegatingFileIndexer(fileIndexerConfigurationProperties);
        PagesStructure pagesStructure = fileIndexer.indexPath(inputDirectory);
        if (pagesStructure.pages().isEmpty()) {
            logger.warn("No files found in input directory. Used file indexer options {}",
                    fileIndexerConfigurationProperties);
        } else {
            PagesStructurePrinter printer = new PagesStructurePrinter();
            printer.prettyPrint(pagesStructure);
        }
        return pagesStructure;
    }

    protected static PageStructureConverter createConverter(ConvertOptions convertOptions) {
        PageStructureTitleProcessor pageStructureTitleProcessor =
                new DefaultPageStructureTitleProcessor(convertOptions.titleExtract,
                        convertOptions.titlePrefix,
                        convertOptions.titleSuffix,
                        convertOptions.titleChildPrefixed);
        boolean needToRemoveTitle = convertOptions.titleRemoveFromContent != null ?
                convertOptions.titleRemoveFromContent :
                convertOptions.titleExtract.equals(TitleExtractStrategy.FROM_FIRST_HEADER);
        PageStructureConverter converterService = null;
        switch (convertOptions.converter) {
            case MD2WIKI:
                converterService = new Md2WikiConverter(pageStructureTitleProcessor, convertOptions.outputDirectory, needToRemoveTitle, convertOptions.plantumlCodeMacroEnable, convertOptions.plantumlCodeMacroName);
                break;
            case NO:
                converterService = new NoopConverter(pageStructureTitleProcessor, needToRemoveTitle);
                break;
            case COPYING:
                converterService = new CopyingConverter(pageStructureTitleProcessor, convertOptions.outputDirectory, needToRemoveTitle);
                break;
        }
        return converterService;
    }

    protected static ConfluenceContentModel convert(PagesStructure pagesStructure, PageStructureConverter converterService) {
        logger.info("Convert using {}", converterService);
        try {
            return converterService.convert(pagesStructure);
        } catch (IOException e) {
            logger.error("Cannot convert provided input dir content with error", e);
            throw new RuntimeException(e);
        }
    }

    public static class IndexerOptions {
        @CommandLine.Option(names = {"--file-extension"}, description = "File extension to index as confluence content pages")
        public String fileExtension = "md"; //todo change fileExtension based on converter
        @CommandLine.Option(names = {"--exclude-pattern"}, description = "Exclude pattern in format of glob:** or regexp:.*. For syntax see javadoc of java.nio.file.FileSystem.getPathMatcher method")
        public String excludePattern = "glob:**/.*";
        @CommandLine.Option(names = {"--indexer-root-page"}, description = "Use specified page as parent page for all another top-level pages in an input directory")
        public String indexerRootPage = null;
        @CommandLine.Option(names = {"--child-layout"}, description =
                "SUB_DIRECTORY is layout when source files for children pages resides in directory with the name equals to basename of parent file\n " +
                        "SAME_DIRECTORY is layout when file with name 'index.md' or 'README.md' is the source file of parent page and other files in the directory are source files for children pages",
                defaultValue = "SUB_DIRECTORY",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        public ChildLayout childLayout = ChildLayout.SUB_DIRECTORY;
    }

    public static class ConvertOptions { //todo split on mandatory and additional
        @CommandLine.Option(names = {"--converter"}, description = "Valid values: ${COMPLETION-CANDIDATES}",
                defaultValue = "MD2WIKI",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        public ConverterType converter = MD2WIKI;
        @CommandLine.Option(names = {"-i", "--input-dir"}, required = true, description = "Input directory")
        public Path inputDirectory;
        @CommandLine.Option(names = {"-o", "--output-dir"}, description = "Output directory")
        public Path outputDirectory;
        @CommandLine.Option(names = {"--title-extract"}, description = "Strategy to extract title from file", //todo rename to TitleExtractFrom
                defaultValue = "FROM_FIRST_HEADER",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        public TitleExtractStrategy titleExtract = TitleExtractStrategy.FROM_FIRST_HEADER;

        @CommandLine.Option(names = {"--title-prefix"}, description = "Title prefix common for all pages")
        public String titlePrefix;
        @CommandLine.Option(names = {"--title-suffix"}, description = "Title suffix common for all pages")
        public String titleSuffix;
        @CommandLine.Option(names = {"--title-child-prefixed"}, description = "Add title prefix of root page if page is a child")
        public boolean titleChildPrefixed;
        @CommandLine.Option(names = {"--title-remove-from-content"}, description = "Remove title from converted content, to avoid duplicate titles rendering in an Confluence")
        public Boolean titleRemoveFromContent;
        @CommandLine.Option(names = {"--plantuml-code-macro-enable"}, description = "Render markdown plantuml fenced code block as confluence plantuml macro (server-side rendering)")
        public Boolean plantumlCodeMacroEnable = false;
        @CommandLine.Option(names = {"--plantuml-code-macro-name"}, description = "Name of confluence macro to render plantuml. Need to Confluence plugin. Possible known options are: 'plantuml' or 'plantumlrender' or 'plantumlcloud'. By default, 'plantuml' is used.")
        public String plantumlCodeMacroName = "plantuml";

    }

    public enum ConverterType {
        NO,
        COPYING,
        MD2WIKI,
        VIEW2MD
    }
}
