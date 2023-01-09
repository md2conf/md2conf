package io.github.md2conf.toolset;

import io.github.md2conf.confluence.client.ConfluenceClientFactory;
import io.github.md2conf.confluence.client.PublishConfluenceClient;
import org.apache.commons.io.file.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import static io.github.md2conf.toolset.PublishCommand.buildConfluenceClientConfigurationProperties;
import static io.github.md2conf.toolset.PublishCommand.loadConfluenceContentModel;

@Command(name = "conpub", aliases = "convert-and-publish", description = "Convert and publish docs to a Confluence instance")
public class ConpubCommand implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @CommandLine.Mixin
    LoggingMixin loggingMixin;
    @CommandLine.ArgGroup(exclusive = false)
    ConvertCommand.ConvertOptions convertOptions;
    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    PublishCommand.MandatoryPublishOptions mandatoryPublishOptions;
    @CommandLine.ArgGroup(exclusive = false, heading = "Additional publish options\n")
    PublishCommand.AdditionalPublishOptions additionalPublishOptions;

    //make output dir parameter computable
    @Override
    public void run() {
        ConvertCommand.convert(convertOptions);
        var model = loadConfluenceContentModel(mandatoryPublishOptions, convertOptions.outputDirectory);
        var clientProps = buildConfluenceClientConfigurationProperties(mandatoryPublishOptions, additionalPublishOptions);
        PublishConfluenceClient confluenceClient = ConfluenceClientFactory.publishConfluenceClient(clientProps, model, null);
        confluenceClient.publish(model, mandatoryPublishOptions.spaceKey, mandatoryPublishOptions.parentPageTitle);
    }

}
