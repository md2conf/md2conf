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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static io.github.md2conf.model.util.ModelFilesystemUtil.saveConfluenceContentModelAtPath;

@CommandLine.Command(name = "md2wiki")
@Slf4j
public class Md2WikiConvertCommand implements Runnable {

    @CommandLine.Mixin
    LoggingMixin loggingMixin;


    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1", heading = "md2wiki converting options:\n")
    private Md2WikiConvertOptions md2WikiConvertOptions;


    @CommandLine.ArgGroup(exclusive = false, heading = "Indexer options:\n", order = 3)
    private IndexCommand.IndexerOptions indexerOptions;


    @Override
    public void run() {
        var indexerOptionsLocal = indexerOptions == null ? new IndexCommand.IndexerOptions() : indexerOptions;
        convertMd2Wiki(this.md2WikiConvertOptions, indexerOptionsLocal);
    }

    @SneakyThrows
    public static File convertMd2Wiki(Md2WikiConvertOptions md2WikiConvertOptions, IndexCommand.IndexerOptions indexerOptions) {
        PagesStructure pagesStructure = IndexCommand.indexInputDirectory(indexerOptions);
        PageStructureConverter converterService = createConverter(md2WikiConvertOptions);
        ConfluenceContentModel model = converterService.convert(pagesStructure);
        File contentModelFile = saveConfluenceContentModelAtPath(model, md2WikiConvertOptions.outputDirectory);
        log.info("Confluence content model saved at file {}", contentModelFile);
        return contentModelFile;
    }

    protected static ConfluenceContentModel convertInternal(PagesStructure pagesStructure, PageStructureConverter converterService) {
        log.info("Convert using {}", converterService);
        try {
            return converterService.convert(pagesStructure);
        } catch (IOException e) {
            log.error("Cannot convert provided input dir content with error", e);
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
        @CommandLine.Option(names = { "--model-path"}, required = false, description = "Model path directory") //todo rework
        public Path modelPath; //todo drop?

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
