package io.github.md2conf.toolset;

import io.github.md2conf.converter.Converter;
import io.github.md2conf.converter.copying.CopyingConverter;
import io.github.md2conf.converter.md2wiki.Md2WikiConverter;
import io.github.md2conf.converter.noop.NoopConverter;
import io.github.md2conf.indexer.DefaultFileIndexer;
import io.github.md2conf.indexer.FileIndexer;
import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.util.ModelReadWriteUtil;
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

import static io.github.md2conf.toolset.ConvertCommand.ConverterType.MD2WIKI;


@Command(name = "convert",
        description = "Convert files to `confluence-content-model` or from `confluence-content-model`")
public class ConvertCommand implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ConvertCommand.class);

    public final static String DEFAULT_OUTPUT = ".md2conf";

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    private ConvertOptions convertOptions;

    @Override
    public void run() {
        convert(this.convertOptions);
    }

    protected static void convert(ConvertOptions convertOptions) {
        initOptionsIfRequired(convertOptions);
        PagesStructure pagesStructure = indexInputDirectory(convertOptions);
        Converter converterService = createConverter(convertOptions);
        ConfluenceContentModel model = convert(pagesStructure, converterService);
        ModelReadWriteUtil.saveConfluenceContentModelToFilesystem(model, convertOptions.outputDirectory);
    }


    protected static void initOptionsIfRequired(ConvertOptions convertOptions) {
        if (convertOptions.outputDirectory == null) {
            logger.warn("Output directory is not specified, default is " + DEFAULT_OUTPUT);
            convertOptions.outputDirectory = new File(convertOptions.inputDirectory.toFile(), DEFAULT_OUTPUT).toPath();
        }
    }

    protected static PagesStructure indexInputDirectory(ConvertOptions convertOptions) {
        FileIndexerConfigurationProperties fileIndexerConfigurationProperties = new FileIndexerConfigurationProperties();
        fileIndexerConfigurationProperties.setFileExtension(convertOptions.fileExtension);
        fileIndexerConfigurationProperties.setExcludePattern(convertOptions.excludePattern);
        FileIndexer fileIndexer = new DefaultFileIndexer(fileIndexerConfigurationProperties);
        PagesStructure pagesStructure = fileIndexer.indexPath(convertOptions.inputDirectory);
        if (pagesStructure.pages().isEmpty()) {
            logger.warn("No files found in input directory. Used file indexer options {}",
                    fileIndexerConfigurationProperties);
        }
        return pagesStructure;
    }

    protected static Converter createConverter(ConvertOptions convertOptions) {
        PageStructureTitleProcessor pageStructureTitleProcessor =
                new DefaultPageStructureTitleProcessor(convertOptions.titleExtract,
                convertOptions.titlePrefix,
                convertOptions.titleSuffix,
                convertOptions.titleChildPrefixed);
        boolean needToRemoveHeaderWithTitle = convertOptions.titleRemoveFromContent!=null ?
                convertOptions.titleRemoveFromContent :
                convertOptions.titleExtract.equals(TitleExtractStrategy.FROM_FIRST_HEADER);
        Converter converterService = null;
        switch (convertOptions.converter) {
            case MD2WIKI:
                converterService = new Md2WikiConverter(pageStructureTitleProcessor, convertOptions.outputDirectory, needToRemoveHeaderWithTitle);
                break;
            case NO:
                converterService = new NoopConverter(pageStructureTitleProcessor, needToRemoveHeaderWithTitle);
                break;
            case COPYING:
                converterService = new CopyingConverter(pageStructureTitleProcessor, convertOptions.outputDirectory, needToRemoveHeaderWithTitle);
                break;
        }
        return converterService;
    }

    protected static ConfluenceContentModel convert(PagesStructure pagesStructure, Converter converterService) {
        try {
            return converterService.convert(pagesStructure);
        } catch (IOException e) {
            logger.error("Cannot convert provided input dir content with error", e);
            throw new RuntimeException(e);
        }
    }

    public static class ConvertOptions { //todo split on mandatory and additional
        @CommandLine.Option(names = {"-c", "--converter"}, description = "Valid values: ${COMPLETION-CANDIDATES}",
                defaultValue = "MD2WIKI",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        protected ConverterType converter = MD2WIKI;
        @CommandLine.Option(names = {"-i", "--input-dir"}, required = true, description = "input directory")
        protected Path inputDirectory;
        @CommandLine.Option(names = {"-o", "--output-dir"}, description = "output directory")
        protected Path outputDirectory;
        @CommandLine.Option(names = {"--file-extension"}, description = "file extension to index as confluence content pages")
        protected String fileExtension = "md"; //todo change fileExtension based on converter
        @CommandLine.Option(names = {"--exclude-pattern"}, description = "Exclude pattern in format of glob:** or regexp:.*. For syntax see javadoc of java.nio.file.FileSystem.getPathMatcher method")
        protected String excludePattern = "glob:**/.*";

        @CommandLine.Option(names = {"-te", "--title-extract"}, description = "Strategy to extract title from file",
                defaultValue = "FROM_FIRST_HEADER",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        protected TitleExtractStrategy titleExtract = TitleExtractStrategy.FROM_FIRST_HEADER;

        @CommandLine.Option(names = {"-tp", "--title-prefix"}, description = "Title prefix common for all pages")
        private String titlePrefix;
        @CommandLine.Option(names = {"-ts","--title-suffix"}, description = "Title suffix common for all pages")
        private String titleSuffix;
        @CommandLine.Option(names = {"-tc", "--title-child-prefixed"}, description = "Add title prefix of root page if page is a child")
        private boolean titleChildPrefixed;
        @CommandLine.Option(names = {"-tr", "--title-remove-from-content"}, description = "Remove title from converted content, to avoid duplicate titles rendering in an Confluence")
        private Boolean titleRemoveFromContent;

    }

    public enum ConverterType {
        NO,
        COPYING,
        MD2WIKI
    }
}
