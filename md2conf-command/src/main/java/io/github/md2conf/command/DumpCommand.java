package io.github.md2conf.command;

import io.github.md2conf.confluence.client.ConfluenceClientFactory;
import io.github.md2conf.confluence.client.DumpConfluenceClient;
import io.github.md2conf.confluence.client.http.ApiInternalClient;
import io.github.md2conf.model.ConfluenceContentModel;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static io.github.md2conf.command.PublishCommand.buildConfluenceClientConfigurationProperties;
import static io.github.md2conf.model.util.ModelFilesystemUtil.saveConfluenceContentModelAtPath;

@CommandLine.Command(name = "dump", description = "Dump content from Confluence instance and save as 'confluence-content-model' with files in Confluence VIEW format")
@Slf4j
public class DumpCommand implements Runnable {
    @CommandLine.Mixin
    LoggingMixin loggingMixin;


    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    PublishCommand.ConfluenceOptions confluenceOptions;

    @CommandLine.Option(names = {"-o", "--output-dir"}, description = "output directory")
    protected Path outputDirectory;

    @Override
    public void run() {
        dump(confluenceOptions, outputDirectory);
    }

    public static void dump(PublishCommand.ConfluenceOptions confluenceOptions, Path outputDirectory) {
        DumpConfluenceClient confluenceClient = prepareConfluenceClient(confluenceOptions, outputDirectory);
        ConfluenceContentModel model = null;
        log.info("Dumping...");
        try {
            model = confluenceClient.dump(confluenceOptions.spaceKey, confluenceOptions.parentPageTitle);
        } catch (IOException e) {
            throw new RuntimeException(e); //improve the code?
        }
        File contentModelFile = saveConfluenceContentModelAtPath(model, outputDirectory);
        log.info("Confluence content model saved at file {}", contentModelFile);
    }

    protected static DumpConfluenceClient prepareConfluenceClient(PublishCommand.ConfluenceOptions confluenceOptions, Path outputDir) {
        var clientProps = buildConfluenceClientConfigurationProperties(confluenceOptions, new PublishCommand.PublishOptions()); //todo drop  PublishOptions
        ApiInternalClient apiInternalClient = ConfluenceClientFactory.createApiInternalClient(clientProps);
        return new DumpConfluenceClient(apiInternalClient, outputDir);
    }

}
