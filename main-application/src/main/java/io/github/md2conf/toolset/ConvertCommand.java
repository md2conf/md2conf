package io.github.md2conf.toolset;

import io.github.md2conf.converter.ConfluencePageFactory;
import io.github.md2conf.converter.Converter;
import io.github.md2conf.converter.ExtractTitleStrategy;
import io.github.md2conf.converter.copying.CopyingConverter;
import io.github.md2conf.converter.md2wiki.Md2WikiConverter;
import io.github.md2conf.converter.noop.NoopConverter;
import io.github.md2conf.indexer.DefaultFileIndexer;
import io.github.md2conf.indexer.FileIndexer;
import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.util.ModelReadWriteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static io.github.md2conf.converter.Converter.Type.MD2WIKI;


@Command(name = "convert",
        description = "Convert files to `confluence-content-model` or from `confluence-content-model`")
public class ConvertCommand implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ConvertCommand.class);

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.ArgGroup(exclusive = false)
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
            String defaultOutput = ".md2conf/out";
            logger.warn("Output directory is not specified, default is " + defaultOutput);
            convertOptions.outputDirectory = new File(convertOptions.inputDirectory.toFile(), defaultOutput).toPath();
        }
    }

    protected static PagesStructure indexInputDirectory(ConvertOptions convertOptions) {
        FileIndexerConfigurationProperties fileIndexerConfigurationProperties = new FileIndexerConfigurationProperties();
        fileIndexerConfigurationProperties.setFileExtension(convertOptions.fileExtension);
        fileIndexerConfigurationProperties.setExcludePattern(convertOptions.excludePattern);
        FileIndexer fileIndexer = new DefaultFileIndexer(fileIndexerConfigurationProperties);
        return fileIndexer.indexPath(convertOptions.inputDirectory);
    }

    protected static Converter createConverter(ConvertOptions convertOptions) {
        ConfluencePageFactory confluencePageFactory = new ConfluencePageFactory(convertOptions.extractTitleStrategy);
        Converter converterService = null;
        switch (convertOptions.converter){
            case MD2WIKI:
                converterService = new Md2WikiConverter(confluencePageFactory, convertOptions.outputDirectory);
            case NO:
                converterService = new NoopConverter(confluencePageFactory);
                break;
            case COPYING:
                converterService= new CopyingConverter(confluencePageFactory, convertOptions.outputDirectory);
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

    public static class ConvertOptions{ //todo split on mandatory and additional
        @CommandLine.Option(names = {"-c", "--converter"}, description = "Valid values: ${COMPLETION-CANDIDATES}",
                defaultValue = "MD2WIKI",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        protected Converter.Type converter = MD2WIKI;
        @CommandLine.Option(names = {"-i", "--input-dir"}, required = true, description = "input directory")
        protected Path inputDirectory;
        @CommandLine.Option(names = {"-o", "--output-dir"}, description = "output directory")
        protected Path outputDirectory;
        @CommandLine.Option(names = {"--file-extension"}, description = "file extension to index as confluence content pages")
        protected String fileExtension = "wiki";
        @CommandLine.Option(names = {"--exclude-pattern"}, description = "Exclude pattern in format of glob:** or regexp:.*. For syntax see javadoc of java.nio.file.FileSystem.getPathMatcher method")
        protected String excludePattern = "glob:**/.*";
        @CommandLine.Option(names = {"-et", "--extract-title-strategy"}, description = "Strategy to extract title from file",
                defaultValue = "FROM_FIRST_HEADER",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        protected ExtractTitleStrategy extractTitleStrategy = ExtractTitleStrategy.FROM_FIRST_HEADER;

    }
}
