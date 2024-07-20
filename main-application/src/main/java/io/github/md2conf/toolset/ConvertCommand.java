package io.github.md2conf.toolset;

import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
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
import static io.github.md2conf.toolset.ConvertCommand.ConverterType.VIEW2MD;
import static io.github.md2conf.toolset.PublishCommand.loadConfluenceContentModel;


@Command(name = "convert-old",
        description = "Convert files to `confluence-content-model` or from `confluence-content-model`")
public class ConvertCommand implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ConvertCommand.class);

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1", heading = "Convert options:\n")
    private Md2WikiConvertCommand.ConvertOptions convertOptions;

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
        convert(this.convertOptions, indexerOptionsLocal, this.confluenceContentModelPath, this.formatOptions);
    }

    public static File convert(Md2WikiConvertCommand.ConvertOptions convertOptions, IndexCommand.IndexerOptions indexerOptions, Path confluenceContentModelPath, View2MdConvertCommand.FormatOptions formatOptions) {
        if (convertOptions.converter == VIEW2MD) {
            ConfluenceContentModel model = loadContentModelFromPathOrDefault(convertOptions, confluenceContentModelPath);
            View2MdConverter view2MdConverter = new View2MdConverter(convertOptions.outputDirectory, formatOptionsAsDataHolder(formatOptions));
            view2MdConverter.convert(model);
            logger.info("Converting to markdown result saved to {}", convertOptions.outputDirectory);
            return null;
        } else {
            PagesStructure pagesStructure = IndexCommand.indexInputDirectory(indexerOptions);
            PageStructureConverter converterService = createConverter(convertOptions);
            ConfluenceContentModel model = convert(pagesStructure, converterService);
            File contentModelFile = saveConfluenceContentModelAtPath(model, convertOptions.outputDirectory);
            logger.info("Confluence content model saved at file {}", contentModelFile);
            return contentModelFile;
        }
    }

    private static DataHolder formatOptionsAsDataHolder(View2MdConvertCommand.FormatOptions formatOptions) {
        MutableDataSet mutableDataSet = new MutableDataSet();
        if (formatOptions!=null) {
            mutableDataSet.set(Formatter.RIGHT_MARGIN, formatOptions.markdownRightMargin);
            mutableDataSet.set(Formatter.HEADING_STYLE, formatOptions.markdownHeadingStyle);
        }
        return mutableDataSet;
    }

    private static ConfluenceContentModel loadContentModelFromPathOrDefault(Md2WikiConvertCommand.ConvertOptions convertOptions, Path confluenceContentModelPath) {
        ConfluenceContentModel model;
        if (confluenceContentModelPath == null || confluenceContentModelPath.toString().equals(".")) {
            model = loadConfluenceContentModel(convertOptions.modelPath);
        } else {
            model = loadConfluenceContentModel(confluenceContentModelPath);
        }
        return model;
    }


    protected static PageStructureConverter createConverter(Md2WikiConvertCommand.ConvertOptions convertOptions) {
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

    @Deprecated
    public enum ConverterType {
        NO,
        COPYING,
        MD2WIKI,
        VIEW2MD
    }
}
