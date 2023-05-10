package io.github.md2conf.toolset;

import io.github.md2conf.confluence.client.ConfluenceClientFactory;
import io.github.md2conf.confluence.client.PublishConfluenceClient;
import io.github.md2conf.model.ConfluenceContentModel;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;

import java.nio.file.Path;

import static io.github.md2conf.toolset.PublishCommand.buildConfluenceClientConfigurationProperties;

@CommandLine.Command(name = "dump",description = "Dump content from Confluence instance")
public class DumpCommand  implements Runnable {
    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    PublishCommand.PublishOptions publishOptions; //todo split & rename ??


    @CommandLine.Option(names = {"-o", "--output-dir"}, description = "output directory")
    protected Path outputDirectory;


    @Override
    public void run() {
        PublishConfluenceClient confluenceClient = prepareConfluenceClient(publishOptions);
     //todo   confluenceClient.publish();
    }

    @NotNull
    protected static PublishConfluenceClient prepareConfluenceClient(PublishCommand.PublishOptions mandatory) { //todo try to generify
        var clientProps = buildConfluenceClientConfigurationProperties(mandatory);
        return ConfluenceClientFactory.publishConfluenceClient(clientProps, new ConfluenceContentModel(), null);

    }

}
