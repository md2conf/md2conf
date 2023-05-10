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
    @CommandLine.ArgGroup(exclusive = false)
    ConvertCommand.ConvertOptions convertOptions;
    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    PublishCommand.PublishOptions publishOptions;

    @Override
    public void run() {
        conpub(convertOptions, publishOptions);
    }

    public static void conpub(ConvertCommand.ConvertOptions convertOptions, PublishCommand.PublishOptions publishOptions) {
        var modelFile = ConvertCommand.convert(convertOptions);
        PublishCommand.publish(publishOptions, modelFile.toPath());
    }

}
