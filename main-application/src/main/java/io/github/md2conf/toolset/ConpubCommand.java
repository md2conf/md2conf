package io.github.md2conf.toolset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "conpub", aliases = "convert-and-publish", description = "Convert and publish docs to a Confluence instance")
public class ConpubCommand implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @CommandLine.Mixin
    LoggingMixin loggingMixin;
    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1", heading = "Confluence options:\n")
    PublishCommand.ConfluenceOptions confluenceOptions;
    @CommandLine.ArgGroup(exclusive = false,  heading = "Publish options:\n")
    PublishCommand.PublishOptions publishOptions;
    @CommandLine.ArgGroup(exclusive = false,  heading = "Indexer options:\n")
    ConvertCommand.IndexerOptions indexerOptions;
    @CommandLine.ArgGroup(exclusive = false,  heading = "Convert options:\n")
    ConvertCommand.ConvertOptions convertOptions;

    @Override
    public void run() {
        var convertOptionsLocal = convertOptions==null? new ConvertCommand.ConvertOptions(): convertOptions;
        var indexerOptionsLocal = indexerOptions==null? new ConvertCommand.IndexerOptions(): indexerOptions;
        var publishOptionsLocal = publishOptions==null? new PublishCommand.PublishOptions(): publishOptions;
        conpub(convertOptionsLocal, indexerOptionsLocal, confluenceOptions, publishOptionsLocal);
    }

    public static void conpub(ConvertCommand.ConvertOptions convertOptions, ConvertCommand.IndexerOptions indexerOptions, PublishCommand.ConfluenceOptions confluenceOptions, PublishCommand.PublishOptions publishOptions) {
        var modelFile = ConvertCommand.convert(convertOptions, indexerOptions, null);
        PublishCommand.publish(confluenceOptions, publishOptions, modelFile.toPath());
    }

}
