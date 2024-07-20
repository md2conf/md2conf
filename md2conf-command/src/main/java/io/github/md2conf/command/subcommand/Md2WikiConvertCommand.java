package io.github.md2conf.command.subcommand;

import io.github.md2conf.command.ConvertCommand;
import io.github.md2conf.command.IndexCommand;
import io.github.md2conf.command.LoggingMixin;
import io.github.md2conf.converter.PageStructureConverter;
import io.github.md2conf.converter.md2wiki.Md2WikiConverter;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.title.processor.DefaultPageStructureTitleProcessor;
import io.github.md2conf.title.processor.PageStructureTitleProcessor;
import io.github.md2conf.title.processor.TitleExtractStrategy;
import io.github.md2conf.title.processor.TitleProcessorOptions;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.File;

import static io.github.md2conf.model.util.ModelFilesystemUtil.saveConfluenceContentModelAtPath;

@CommandLine.Command(name = "md2wiki")
@Slf4j
public class Md2WikiConvertCommand implements Runnable {

    @CommandLine.Mixin
    LoggingMixin loggingMixin;
    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1", heading = "md2wiki converting options:\n")
    private Md2WikiConvertOptions md2WikiConvertOptions;
    @CommandLine.ArgGroup(exclusive = false, heading = "Indexer options:\n", order = 2)
    private IndexCommand.IndexerOptions indexerOptions;
    @CommandLine.ArgGroup(exclusive = false, heading = "Title processing options:\n", order = 3)
    private ConvertCommand.TitleProcessingOptions titleProcessingOptions;


    @Override
    public void run() {
        var indexerOptionsLocal = indexerOptions == null ? new IndexCommand.IndexerOptions() : indexerOptions;
        var titleProcessingLocal = titleProcessingOptions == null ? new ConvertCommand.TitleProcessingOptions() : titleProcessingOptions;
        convertMd2Wiki(this.md2WikiConvertOptions, indexerOptionsLocal, titleProcessingLocal);
    }

    @SneakyThrows
    public static File convertMd2Wiki(Md2WikiConvertOptions md2WikiConvertOptions,
                                      IndexCommand.IndexerOptions indexerOptions,
                                      ConvertCommand.TitleProcessingOptions titleProcessingOptions) {
        PagesStructure pagesStructure = IndexCommand.indexInputDirectory(indexerOptions);
        PageStructureConverter converterService = createConverter(md2WikiConvertOptions, titleProcessingOptions);
        ConfluenceContentModel model = converterService.convert(pagesStructure);
        File contentModelFile = saveConfluenceContentModelAtPath(model, md2WikiConvertOptions.outputDirectory);
        log.info("Confluence content model saved at file {}", contentModelFile);
        return contentModelFile;
    }

    private static PageStructureConverter createConverter(Md2WikiConvertOptions md2WikiConvertOptions,
                                                          ConvertCommand.TitleProcessingOptions titleProcessingOptions) {

        PageStructureTitleProcessor pageStructureTitleProcessor =
                new DefaultPageStructureTitleProcessor(TitleProcessorOptions.builder()
                        .titleExtractStrategy(titleProcessingOptions.titleExtract)
                        .titlePrefix(titleProcessingOptions.titlePrefix)
                        .titleSuffix(titleProcessingOptions.titleSuffix)
                        .titleChildPrefixed(titleProcessingOptions.titleChildPrefixed)
                        .build());
        boolean needToRemoveTitle = titleProcessingOptions.titleExtract.equals(TitleExtractStrategy.FROM_FIRST_HEADER);

        return new Md2WikiConverter(pageStructureTitleProcessor,
                md2WikiConvertOptions.outputDirectory, needToRemoveTitle,
                md2WikiConvertOptions.plantumlCodeMacroEnable,
                md2WikiConvertOptions.plantumlCodeMacroName);
    }

    public static class Md2WikiConvertOptions extends ConvertCommand.ConvertOptions {
        @CommandLine.Option(names = {"--plantuml-code-macro-enable"}, description = "Render markdown plantuml fenced code block as confluence plantuml macro (server-side rendering)")
        public Boolean plantumlCodeMacroEnable = false;
        @CommandLine.Option(names = {"--plantuml-code-macro-name"}, description = "Name of confluence macro to render plantuml. Need to have custom Confluence plugin on a server. Possible known options are: 'plantuml' or 'plantumlrender' or 'plantumlcloud'. By default, 'plantuml' is used.")
        public String plantumlCodeMacroName = "plantuml";
    }
}
