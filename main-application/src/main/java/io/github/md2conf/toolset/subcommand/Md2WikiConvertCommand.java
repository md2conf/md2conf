package io.github.md2conf.toolset.subcommand;

import io.github.md2conf.converter.PageStructureConverter;
import io.github.md2conf.converter.md2wiki.Md2WikiConverter;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.title.processor.DefaultPageStructureTitleProcessor;
import io.github.md2conf.title.processor.PageStructureTitleProcessor;
import io.github.md2conf.title.processor.TitleExtractStrategy;
import io.github.md2conf.toolset.ConvertCommand;
import io.github.md2conf.toolset.IndexCommand;
import io.github.md2conf.toolset.LoggingMixin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static io.github.md2conf.model.util.ModelReadWriteUtil.saveConfluenceContentModelAtPath;
import static io.github.md2conf.toolset.ConvertCommand.ConverterType.MD2WIKI;

@CommandLine.Command(name = "md2wiki")
public class Md2WikiConvertCommand implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(ConvertCommand.class);

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1", heading = "Convert options:\n")
    private Md2WikiConvertCommand.ConvertOptions convertOptions;

    @CommandLine.ArgGroup(exclusive = false, heading = "Indexer options:\n", order = 3)
    private IndexCommand.IndexerOptions indexerOptions;

    @Override
    public void run() {
        var indexerOptionsLocal = indexerOptions == null ? new IndexCommand.IndexerOptions() : indexerOptions;
        try {
            convert(this.convertOptions, indexerOptionsLocal);
        } catch (IOException e) {
            throw new RuntimeException(e); //todo use lombok
        }
    }

    public static File convert(ConvertOptions convertOptions, IndexCommand.IndexerOptions indexerOptions) throws IOException {
        PagesStructure pagesStructure = IndexCommand.indexInputDirectory(indexerOptions);
        PageStructureConverter converterService = createConverterPr(convertOptions);
        ConfluenceContentModel model = converterService.convert(pagesStructure);
        File contentModelFile = saveConfluenceContentModelAtPath(model, convertOptions.outputDirectory);
        logger.info("Confluence content model saved at file {}", contentModelFile);
        return contentModelFile;
    }

    protected static ConfluenceContentModel convertInternal(PagesStructure pagesStructure, PageStructureConverter converterService) {
        logger.info("Convert using {}", converterService);
        try {
            return converterService.convert(pagesStructure);
        } catch (IOException e) {
            logger.error("Cannot convert provided input dir content with error", e);
            throw new RuntimeException(e);
        }
    }

    private static PageStructureConverter createConverterPr(ConvertOptions convertOptions) {
        PageStructureTitleProcessor pageStructureTitleProcessor =
                new DefaultPageStructureTitleProcessor(convertOptions.titleExtract,
                        convertOptions.titlePrefix,
                        convertOptions.titleSuffix,
                        convertOptions.titleChildPrefixed);
        boolean needToRemoveTitle = convertOptions.titleRemoveFromContent != null ?
                convertOptions.titleRemoveFromContent :
                convertOptions.titleExtract.equals(TitleExtractStrategy.FROM_FIRST_HEADER);

        return new Md2WikiConverter(pageStructureTitleProcessor,
                convertOptions.outputDirectory, needToRemoveTitle,
                convertOptions.plantumlCodeMacroEnable,
                convertOptions.plantumlCodeMacroName);
    }

    public static class ConvertOptions { //todo split on mandatory and additional
        @Deprecated
        @CommandLine.Option(names = {"--converter"}, description = "Valid values: ${COMPLETION-CANDIDATES}",
                defaultValue = "MD2WIKI",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        public ConvertCommand.ConverterType converter = MD2WIKI;
        @CommandLine.Option(names = { "--model-path"}, required = false, description = "Model path directory") //todo rework
        public Path modelPath;
        @CommandLine.Option(names = {"-o", "--output-dir"}, required = true, description = "Output directory")
        public Path outputDirectory;

        //todo extact to TitleOptions and make common for both converters
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
        @CommandLine.Option(names = {"--plantuml-code-macro-name"}, description = "Name of confluence macro to render plantuml. Need to have custom Confluence plugin on a server. Possible known options are: 'plantuml' or 'plantumlrender' or 'plantumlcloud'. By default, 'plantuml' is used.")
        public String plantumlCodeMacroName = "plantuml";
    }
}
