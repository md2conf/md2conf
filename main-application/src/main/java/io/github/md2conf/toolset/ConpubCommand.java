package io.github.md2conf.toolset;

import io.github.md2conf.toolset.subcommand.Md2WikiConvertCommand;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "conpub", aliases = "convert-and-publish", description = "Convert and publish docs to a Confluence instance")
public class ConpubCommand implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @CommandLine.Mixin
    LoggingMixin loggingMixin;
    @CommandLine.ArgGroup(exclusive = false,  heading = "Indexer options:\n")
    IndexCommand.IndexerOptions indexerOptions;
    @CommandLine.ArgGroup(exclusive = false,  heading = "Convert options:\n")
    Md2WikiConvertCommand.Md2WikiConvertOptions md2WikiConvertOptions;
    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1", heading = "Confluence options:\n")
    PublishCommand.ConfluenceOptions confluenceOptions;
    @CommandLine.ArgGroup(exclusive = false,  heading = "Publish options:\n")
    PublishCommand.PublishOptions publishOptions;

    @Override
    public void run() {
        var convertOptionsLocal = md2WikiConvertOptions ==null? new Md2WikiConvertCommand.Md2WikiConvertOptions(): md2WikiConvertOptions;
        var indexerOptionsLocal = indexerOptions==null? new IndexCommand.IndexerOptions(): indexerOptions;
        var publishOptionsLocal = publishOptions==null? new PublishCommand.PublishOptions(): publishOptions;
        conpub(convertOptionsLocal, indexerOptionsLocal, confluenceOptions, publishOptionsLocal);
    }

    @SneakyThrows
    public static void conpub(Md2WikiConvertCommand.Md2WikiConvertOptions md2WikiConvertOptions,
                              IndexCommand.IndexerOptions indexerOptions,
                              PublishCommand.ConfluenceOptions confluenceOptions,
                              PublishCommand.PublishOptions publishOptions) {
        var modelFile = Md2WikiConvertCommand.convertMd2Wiki(md2WikiConvertOptions, indexerOptions);
        PublishCommand.publish(confluenceOptions, publishOptions, modelFile.toPath());
    }

}
