package io.github.md2conf.toolset.subcommand;

import io.github.md2conf.converter.PageStructureConverter;
import io.github.md2conf.converter.md2wiki.Md2WikiConverter;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.title.processor.DefaultPageStructureTitleProcessor;
import io.github.md2conf.title.processor.PageStructureTitleProcessor;
import io.github.md2conf.title.processor.TitleExtractStrategy;
import io.github.md2conf.toolset.ConvertCommand;
import io.github.md2conf.toolset.ConvertOldCommand;
import io.github.md2conf.toolset.IndexCommand;
import io.github.md2conf.toolset.LoggingMixin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;

import static io.github.md2conf.model.util.ModelReadWriteUtil.saveConfluenceContentModelAtPath;
import static io.github.md2conf.toolset.ConvertOldCommand.ConverterType.MD2WIKI;

@CommandLine.Command(name = "md2wiki")
public class Md2WikiConvertCommand implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(ConvertOldCommand.class);

    @CommandLine.Mixin
    LoggingMixin loggingMixin;


    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1", heading = "md2wiki options:\n")
    private Md2WikiConvertOptions md2WikiConvertOptions;


    @CommandLine.ArgGroup(exclusive = false, heading = "Indexer options:\n", order = 3)
    private IndexCommand.IndexerOptions indexerOptions;


    @Override
    public void run() {
        var indexerOptionsLocal = indexerOptions == null ? new IndexCommand.IndexerOptions() : indexerOptions;
        try {
            convertMd2Wiki(this.md2WikiConvertOptions, indexerOptionsLocal);
        } catch (IOException e) {
            throw new RuntimeException(e); //todo use lombok
        }
    }

    public static File convertMd2Wiki(Md2WikiConvertOptions md2WikiConvertOptions, IndexCommand.IndexerOptions indexerOptions) throws IOException {
        PagesStructure pagesStructure = IndexCommand.indexInputDirectory(indexerOptions);
        PageStructureConverter converterService = createConverter(md2WikiConvertOptions);
        ConfluenceContentModel model = converterService.convert(pagesStructure);
        File contentModelFile = saveConfluenceContentModelAtPath(model, md2WikiConvertOptions.outputDirectory);
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

    private static PageStructureConverter createConverter(Md2WikiConvertOptions md2WikiConvertOptions) {
        PageStructureTitleProcessor pageStructureTitleProcessor =
                new DefaultPageStructureTitleProcessor(md2WikiConvertOptions.titleExtract,
                        md2WikiConvertOptions.titlePrefix,
                        md2WikiConvertOptions.titleSuffix,
                        md2WikiConvertOptions.titleChildPrefixed);
        boolean needToRemoveTitle = md2WikiConvertOptions.titleRemoveFromContent != null ?
                md2WikiConvertOptions.titleRemoveFromContent :
                md2WikiConvertOptions.titleExtract.equals(TitleExtractStrategy.FROM_FIRST_HEADER);

        return new Md2WikiConverter(pageStructureTitleProcessor,
                md2WikiConvertOptions.outputDirectory, needToRemoveTitle,
                md2WikiConvertOptions.plantumlCodeMacroEnable,
                md2WikiConvertOptions.plantumlCodeMacroName);
    }

    public static class Md2WikiConvertOptions extends ConvertCommand.ConvertOptions{ //todo split on mandatory and additional
        @Deprecated
        @CommandLine.Option(names = {"--converter"}, description = "Valid values: ${COMPLETION-CANDIDATES}",
                defaultValue = "MD2WIKI",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        public ConvertOldCommand.ConverterType converter = MD2WIKI;

        //todo extract to TitleOptions and make common for both converters
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
