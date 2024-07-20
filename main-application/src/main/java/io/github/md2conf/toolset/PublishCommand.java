package io.github.md2conf.toolset;

import io.github.md2conf.confluence.client.ConfluenceClientConfigurationProperties;
import io.github.md2conf.confluence.client.ConfluenceClientFactory;
import io.github.md2conf.confluence.client.OrphanRemovalStrategy;
import io.github.md2conf.confluence.client.PublishingStrategy;
import io.github.md2conf.model.ConfluenceContentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.nio.file.Path;

import static io.github.md2conf.confluence.client.ConfluenceClientConfigurationProperties.ConfluenceClientConfigurationPropertiesBuilder.aConfluenceClientConfigurationProperties;
import static io.github.md2conf.model.util.ModelReadWriteUtil.readFromYamlOrJson;
import static io.github.md2conf.toolset.ConvertCommand.findFilePathWithModel;

@Command(name = "publish", description = "Publish content to a Confluence instance", sortOptions = false)
public class PublishCommand implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PublishCommand.class);

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    protected ConfluenceOptions confluenceOptions;

    @CommandLine.ArgGroup(exclusive = false)
    protected PublishOptions publishOptions;

    @CommandLine.Option(names = { "-m", "--confluence-content-model"}, description = "Path to file with `confluence-content-model` JSON file or to directory with confluence-content-model.json file. Default value is current working directory.", defaultValue = ".", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    public Path confluenceContentModelPath;
    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @Override
    public void run() {
        PublishOptions publishOptionsLocal = publishOptions==null ? new PublishOptions(): publishOptions;
        publish(confluenceOptions, publishOptionsLocal, confluenceContentModelPath);
    }

    public static void publish(ConfluenceOptions confluenceOptions, PublishOptions publishOptions, Path confluenceContentModelPath) {
        var model = loadConfluenceContentModel(confluenceContentModelPath);
        var clientProps = buildConfluenceClientConfigurationProperties(confluenceOptions, publishOptions);
        var publishConfluenceClient = ConfluenceClientFactory.publishConfluenceClient(clientProps, model, null);
        publishConfluenceClient.publish(model, confluenceOptions.spaceKey, confluenceOptions.parentPageTitle);
    }


    protected static ConfluenceClientConfigurationProperties buildConfluenceClientConfigurationProperties(ConfluenceOptions confluenceOptions, PublishOptions publishOptions) {
        return aConfluenceClientConfigurationProperties()
                .withConfluenceUrl(confluenceOptions.confluenceUrl)
                .withParentPageTitle(confluenceOptions.parentPageTitle)
                .withPasswordOrPersonalAccessToken(confluenceOptions.password)
                .withSpaceKey(confluenceOptions.spaceKey)
                .withUsername(confluenceOptions.username)
                .withMaxRequestsPerSecond(confluenceOptions.maxRequestsPerSecond)
                .withConnectionTTL(confluenceOptions.connectionTimeToLive)
                .withVersionMessage(publishOptions.versionMessage)
                .withSkipSslVerification(confluenceOptions.skipSslVerification)
                .withNotifyWatchers(publishOptions.notifyWatchers)
                .withOrphanRemovalStrategy(publishOptions.orphanRemovalStrategy)
                .withPublishingStrategy(publishOptions.parentPagePublishingStrategy)
                .build();
    }

    protected static ConfluenceContentModel loadConfluenceContentModel(Path confluenceContentModelPath) {
        Path modelFilePath;
        if (confluenceContentModelPath.toFile().isDirectory()){
            modelFilePath = findFilePathWithModel(confluenceContentModelPath);
        }else{
            modelFilePath = confluenceContentModelPath;
        }
        if (!modelFilePath.toFile().exists()) {
            throw new IllegalArgumentException("File doesn't exists at path " + modelFilePath);
        }
        return readFromYamlOrJson(modelFilePath.toFile());
    }



    public static class ConfluenceOptions{
        @CommandLine.Option(names = {"-url", "--confluence-url"}, required = true, description = "The root URL of the Confluence instance", order = 1)
        public String confluenceUrl;
        @CommandLine.Option(names = {/*"-user",*/ "--username"}, description = "Username of the Confluence user", order = 2)
        public String username;
        @CommandLine.Option(names = {/*"-p",*/ "--password"}, description = "The password or personal access token of the user. In case of using token don't specify username.", order = 3)
        public String password;
        @CommandLine.Option(names = {"-s", "--space-key"}, required = true, description = "The key of the Confluence space", order = 4)
        public String spaceKey;
        @CommandLine.Option(names = {"-pt", "--parent-page-title"}, required = true, description = "The parent page to publish `confluence-content-model`", order = 5)
        public String parentPageTitle;
        @CommandLine.Option(names = {"--skip-ssl-verification"}, defaultValue = "false", showDefaultValue = CommandLine.Help.Visibility.ALWAYS, order = 6)
        public boolean skipSslVerification = false;
        @CommandLine.Option(names = {"--max-requests-per-second"}, order = 7)
        public Double maxRequestsPerSecond;
        @CommandLine.Option(names = {"--connection-time-to-live"}, description = "Connection TTL in milliseconds", order = 8)
        public Integer connectionTimeToLive;
    }

    public static class PublishOptions {
        @CommandLine.Option(names = {"--orphan-removal-strategy"}, description = "Valid values: ${COMPLETION-CANDIDATES}",
                defaultValue = "KEEP_ORPHANS",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS, order = 11)
        public OrphanRemovalStrategy orphanRemovalStrategy;
        @CommandLine.Option(names = {"--parent-page-publishing-strategy"}, description = "Valid values: ${COMPLETION-CANDIDATES}",
                defaultValue = "APPEND_TO_ANCESTOR",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS, order = 12)
        public PublishingStrategy parentPagePublishingStrategy = PublishingStrategy.APPEND_TO_ANCESTOR;
        @CommandLine.Option(names = {"--notify-watchers"}, defaultValue = "false", showDefaultValue = CommandLine.Help.Visibility.ALWAYS, order = 13)
        public boolean notifyWatchers = false;
        @CommandLine.Option(names = {"--version-message"}, description = "Version message", defaultValue = "Published by md2conf", showDefaultValue = CommandLine.Help.Visibility.ALWAYS, order = 14)
        public String versionMessage = "Published by md2conf";
    }

}
