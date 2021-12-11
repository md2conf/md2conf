package io.github.md2conf.toolset;

import io.github.md2conf.confluence.client.ConfluenceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "conpub", aliases = "convert-and-convert", description = "Convert and convert docs to a Confluence instance")
public class ConpubCommand implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @CommandLine.Mixin
    LoggingMixin loggingMixin;
    @CommandLine.ArgGroup(exclusive = false)
    ConvertCommand.ConvertOptions convertOptions;
    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    PublishCommand.MandatoryPublishOptions mandatoryPublishOptions;
    @CommandLine.ArgGroup(exclusive = false, heading = "Additional convert options\n")
    PublishCommand.AdditionalPublishOptions additionalPublishOptions;

    @Override
    public void run() {
        ConvertCommand.convert(convertOptions);
        ConfluenceClient confluenceClient = PublishCommand.prepareConfluenceClient(mandatoryPublishOptions, additionalPublishOptions);
        confluenceClient.publish();
    }

}
