package io.github.md2conf.toolset;

import io.github.md2conf.confluence.client.ConfluenceClientConfigurationProperties;
import io.github.md2conf.confluence.client.ConfluenceClientFactory;
import io.github.md2conf.confluence.client.OrphanRemovalStrategy;
import io.github.md2conf.model.ConfluenceContentModel;
import org.apache.commons.io.file.PathUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.nio.file.Path;

import static io.github.md2conf.confluence.client.ConfluenceClientConfigurationProperties.ConfluenceClientConfigurationPropertiesBuilder.aConfluenceClientConfigurationProperties;
import static io.github.md2conf.model.util.ModelReadWriteUtil.readFromYamlOrJson;

@Command(name = "publish", description = "Publish content to a Confluence instance", sortOptions = false)
public class PublishCommand implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PublishCommand.class);
    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    protected MandatoryPublishOptions mandatory;
    @CommandLine.ArgGroup(exclusive = false, heading = "Additional publish options\n")
    protected AdditionalPublishOptions additional;
    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @Override
    public void run() {
        var model = loadConfluenceContentModel(mandatory);
        var clientProps = buildConfluenceClientConfigurationProperties(mandatory, additional);
        var publishConfluenceClient = ConfluenceClientFactory.publishConfluenceClient(clientProps, model, null); //todo listener
        publishConfluenceClient.publish(model, mandatory.spaceKey, mandatory.parentPageTitle);
    }



    protected static ConfluenceClientConfigurationProperties buildConfluenceClientConfigurationProperties(MandatoryPublishOptions options, AdditionalPublishOptions additional){
        var propertiesBuilder = aConfluenceClientConfigurationProperties()
                .withConfluenceUrl(options.confluenceUrl)
                .withParentPageTitle(options.parentPageTitle)
                .withPasswordOrPersonalAccessToken(options.password)
                .withSpaceKey(options.spaceKey)
                .withUsername(options.username);
        if (additional != null) {
            propertiesBuilder.withMaxRequestsPerSecond(additional.maxRequestsPerSecond)
                             .withVersionMessage(additional.versionMessage)
                             .withSkipSslVerification(additional.skipSslVerification)
                             .withNotifyWatchers(additional.notifyWatchers)
                             .withOrphanRemovalStrategy(additional.orphanRemovalStrategy);
        }
        return propertiesBuilder.build();
    }

    protected static ConfluenceContentModel loadConfluenceContentModel(MandatoryPublishOptions options) {
        Path path = findFilePathWithModel(options);
        if (!path.toFile().exists()) {
            throw new IllegalArgumentException("File doesn't exists at path " + path);
        }
        return readFromYamlOrJson(path.toFile());
    }

    @NotNull
    private static Path findFilePathWithModel(MandatoryPublishOptions options) {
        Path result = options.confluenceContentModelPath;
        if (result==null){
            logger.info("No path to confluence content model provided, will load from current directory using default name");
            result = PathUtils.current().resolve(ConfluenceContentModel.DEFAULT_FILE_NAME);
        }
        return  result;
    }

    public static class MandatoryPublishOptions {
        @CommandLine.Option(names = {"-url", "--confluence-url"}, required = true, description = "The root URL of the Confluence instance", order = 1)
        protected String confluenceUrl;
        @CommandLine.Option(names = {/*"-user",*/ "--username"}, description = "Username of the Confluence user", order = 2)
        protected String username;
        @CommandLine.Option(names = {/*"-p",*/ "--password"}, description = "The password or personal access token of the user", order = 3)
        protected String password;
        @CommandLine.Option(names = {"-s", "--space-key"}, required = true, description = "The key of the Confluence space", order = 4)
        protected String spaceKey;
        @CommandLine.Option(names = {"-pt", "--parent-page-title"}, required = true, description = "The parent page to publish `confluence-content-model`", order = 5)
        protected String parentPageTitle;
        @CommandLine.Option(names = {"-m", "--confluence-content-model"}, description = "Path to file with `confluence-content-model` JSON file or to directory with confluence-content-model.json file.")
        protected Path confluenceContentModelPath; //todo move
    }

    public static class AdditionalPublishOptions {
        @CommandLine.Option(names = {"--version-message"}, description = "Version message", defaultValue = "Published by md2conf", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        protected String versionMessage = "Published by md2conf";
        @CommandLine.Option(names = {"--orphan-removal-strategy"}, description = "Valid values: ${COMPLETION-CANDIDATES}",
                defaultValue = "KEEP_ORPHANS",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        protected OrphanRemovalStrategy orphanRemovalStrategy;
        @CommandLine.Option(names = {"--notify-watchers"}, defaultValue = "false", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        protected boolean notifyWatchers = false;
        @CommandLine.Option(names = {"--skip-ssl-verification"}, defaultValue = "false", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        protected boolean skipSslVerification = false;
        @CommandLine.Option(names = {"--max-requests-per-second"})
        protected Double maxRequestsPerSecond;
    }
}
