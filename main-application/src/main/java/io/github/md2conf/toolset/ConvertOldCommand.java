package io.github.md2conf.toolset;

import io.github.md2conf.converter.PageStructureConverter;
import io.github.md2conf.converter.copying.CopyingConverter;
import io.github.md2conf.converter.md2wiki.Md2WikiConverter;
import io.github.md2conf.converter.noop.NoopConverter;
import io.github.md2conf.converter.view2md.View2MdConverter;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.title.processor.DefaultPageStructureTitleProcessor;
import io.github.md2conf.title.processor.PageStructureTitleProcessor;
import io.github.md2conf.title.processor.TitleExtractStrategy;
import io.github.md2conf.toolset.subcommand.Md2WikiConvertCommand;
import io.github.md2conf.toolset.subcommand.View2MdConvertCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static io.github.md2conf.model.util.ModelReadWriteUtil.saveConfluenceContentModelAtPath;
import static io.github.md2conf.toolset.ConvertOldCommand.ConverterType.VIEW2MD;
import static io.github.md2conf.toolset.PublishCommand.loadConfluenceContentModel;
import static io.github.md2conf.toolset.subcommand.View2MdConvertCommand.formatOptionsAsDataHolder;


@Command(name = "convert-old",
        description = "Convert files to `confluence-content-model` or from `confluence-content-model`")
public class ConvertOldCommand implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ConvertOldCommand.class);

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1", heading = "Convert options:\n")
    private Md2WikiConvertCommand.Md2WikiConvertOptions md2WikiConvertOptions;

    @CommandLine.ArgGroup(exclusive = false, heading = "Indexer options:\n", order = 3)
    private IndexCommand.IndexerOptions indexerOptions;

    @CommandLine.ArgGroup(exclusive = false, heading = "Format options for view2md converter:\n", order = 4)
    private View2MdConvertCommand.FormatOptions formatOptions;

    //todo adjust description
    @CommandLine.Option(names = {"-m", "--confluence-content-model"}, description = "Path to file with `confluence-content-model` JSON file or to directory with confluence-content-model.json file. Default value is current working directory.", defaultValue = ".", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    public Path confluenceContentModelPath;

    @Override
    public void run() {
        var indexerOptionsLocal = indexerOptions == null ? new IndexCommand.IndexerOptions() : indexerOptions;
        convert(this.md2WikiConvertOptions, indexerOptionsLocal, this.confluenceContentModelPath, this.formatOptions);
    }

    public static File convert(Md2WikiConvertCommand.Md2WikiConvertOptions md2WikiConvertOptions, IndexCommand.IndexerOptions indexerOptions, Path confluenceContentModelPath, View2MdConvertCommand.FormatOptions formatOptions) {
        if (md2WikiConvertOptions.converter == VIEW2MD) {
            ConfluenceContentModel model = loadContentModelFromPathOrDefault(md2WikiConvertOptions, confluenceContentModelPath);
            View2MdConverter view2MdConverter = new View2MdConverter(md2WikiConvertOptions.outputDirectory, formatOptionsAsDataHolder(formatOptions));
            view2MdConverter.convert(model);
            logger.info("Converting to markdown result saved to {}", md2WikiConvertOptions.outputDirectory);
            return null;
        } else {
            PagesStructure pagesStructure = IndexCommand.indexInputDirectory(indexerOptions);
            PageStructureConverter converterService = createConverter(md2WikiConvertOptions);
            ConfluenceContentModel model = convert(pagesStructure, converterService);
            File contentModelFile = saveConfluenceContentModelAtPath(model, md2WikiConvertOptions.outputDirectory);
            logger.info("Confluence content model saved at file {}", contentModelFile);
            return contentModelFile;
        }
    }



    private static ConfluenceContentModel loadContentModelFromPathOrDefault(Md2WikiConvertCommand.Md2WikiConvertOptions md2WikiConvertOptions, Path confluenceContentModelPath) {
        ConfluenceContentModel model;
        if (confluenceContentModelPath == null || confluenceContentModelPath.toString().equals(".")) {
            model = loadConfluenceContentModel(md2WikiConvertOptions.modelPath);
        } else {
            model = loadConfluenceContentModel(confluenceContentModelPath);
        }
        return model;
    }


    protected static PageStructureConverter createConverter(Md2WikiConvertCommand.Md2WikiConvertOptions md2WikiConvertOptions) {
        PageStructureTitleProcessor pageStructureTitleProcessor =
                new DefaultPageStructureTitleProcessor(md2WikiConvertOptions.titleExtract,
                        md2WikiConvertOptions.titlePrefix,
                        md2WikiConvertOptions.titleSuffix,
                        md2WikiConvertOptions.titleChildPrefixed);
        boolean needToRemoveTitle = md2WikiConvertOptions.titleRemoveFromContent != null ?
                md2WikiConvertOptions.titleRemoveFromContent :
                md2WikiConvertOptions.titleExtract.equals(TitleExtractStrategy.FROM_FIRST_HEADER);
        PageStructureConverter converterService = null;
        switch (md2WikiConvertOptions.converter) {
            case MD2WIKI:
                converterService = new Md2WikiConverter(pageStructureTitleProcessor, md2WikiConvertOptions.outputDirectory, needToRemoveTitle, md2WikiConvertOptions.plantumlCodeMacroEnable, md2WikiConvertOptions.plantumlCodeMacroName);
                break;
            case NO:
                converterService = new NoopConverter(pageStructureTitleProcessor, needToRemoveTitle);
                break;
            case COPYING:
                converterService = new CopyingConverter(pageStructureTitleProcessor, md2WikiConvertOptions.outputDirectory, needToRemoveTitle);
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

    @Deprecated
    public enum ConverterType {
        NO,
        COPYING,
        MD2WIKI,
        VIEW2MD
    }
}
