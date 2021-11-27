package io.github.md2conf.toolset;

import io.github.md2conf.confluence.client.ConfluenceClient;
import io.github.md2conf.confluence.client.ConfluenceClientFactory;
import io.github.md2conf.confluence.client.OrphanRemovalStrategy;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.util.ModelReadWriteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static io.github.md2conf.confluence.client.ConfluenceClientConfigurationProperties.ConfluenceClientConfigurationPropertiesBuilder.aConfluenceClientConfigurationProperties;

@Command(name = "publish", description = "Publish content to a Confluence instance", sortOptions = false)
public class PublishCommand implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.Option(names = {"-url", "--confluence-url"}, required = true, description = "The root URL of the Confluence instance", order = 1)
    private String confluenceUrl;
    @CommandLine.Option(names = {/*"-user",*/ "--username"}, description = "Username of the Confluence user", order = 2)
    private String username;
    @CommandLine.Option(names = {/*"-p",*/ "--password"}, description = "The password or personal access token of the user", order = 3)
    private String password;
    @CommandLine.Option(names = {"-s", "--space-key"}, required = true, description = "The key of the Confluence space", order = 4)
    private String spaceKey;
    @CommandLine.Option(names = {"-pt", "--parent-page-title"}, required = true, description = "The parent page to publish `confluence-content-model`", order = 5)
    private String parentPageTitle;

    @CommandLine.Option(names = {"-m", "--confluence-content-model"}, description = "Path to file with `confluence-content-model` JSON file or to directory with confluence-content-model.json file.")
    private Path confluenceContentModelPath;


    @CommandLine.ArgGroup(exclusive = false, heading = "Additional options\n", validate = true)
    private Additional additional;

    public static class Additional {
        @CommandLine.Option(names = {"--version-message"}, description = "Version message", defaultValue = "Published by md2conf", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private String versionMessage = "Published by md2conf";
        @CommandLine.Option(names = {"--orphan-removal-strategy"}, description = "Valid values: ${COMPLETION-CANDIDATES}",
                defaultValue = "KEEP_ORPHANS",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private OrphanRemovalStrategy orphanRemovalStrategy;
        @CommandLine.Option(names = {"--notify-watchers"}, defaultValue = "false", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private boolean notifyWatchers = false;
        @CommandLine.Option(names = {"--skip-ssl-verification"}, defaultValue = "false", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private boolean skipSslVerification = false;
        @CommandLine.Option(names = {"--max-requests-per-second"})
        private Double maxRequestsPerSecond;
    }

    private ConfluenceContentModel tryToFindAndLoad() {
        ConfluenceContentModel res = null;
        File file;
        if (confluenceContentModelPath == null) {
            logger.info("try to find {} in working directory", ConfluenceContentModel.DEFAULT_FILE_NAME);
            file = new File(ConfluenceContentModel.DEFAULT_FILE_NAME);
        } else {
            if (confluenceContentModelPath.toFile().exists()) {
                file = confluenceContentModelPath.toFile();
            } else {
                logger.error("path {} doesn't exists", confluenceContentModelPath);
                throw new IllegalArgumentException("provided confluenceContentModelPath doesn't exists");
            }
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("File doesn't exists " + file);
        }
        try {
            res = ModelReadWriteUtil.readFromYamlOrJson(file);
        } catch (IOException e) {
            logger.error("Cannot parse", e);
        }
        return res;
    }

    @Override
    public void run() {
        var model = tryToFindAndLoad();
        var propertiesBuilder = aConfluenceClientConfigurationProperties()
                .withConfluenceUrl(confluenceUrl)
                .withParentPageTitle(parentPageTitle)
                .withPasswordOrPersonalAccessToken(password)
                .withSpaceKey(spaceKey)
                .withUsername(username);
        if (this.additional != null) {
            propertiesBuilder.withMaxRequestsPerSecond(additional.maxRequestsPerSecond)
                             .withVersionMessage(additional.versionMessage)
                             .withSkipSslVerification(additional.skipSslVerification)
                             .withNotifyWatchers(additional.notifyWatchers)
                             .withOrphanRemovalStrategy(additional.orphanRemovalStrategy);
        }
        ConfluenceClient confluenceClient = ConfluenceClientFactory.confluenceClient(propertiesBuilder.build(), model, null); //todo listener
        confluenceClient.publish();
    }
}
