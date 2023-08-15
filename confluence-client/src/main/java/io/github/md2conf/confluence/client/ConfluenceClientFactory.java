package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.http.ApiInternalClient;
import io.github.md2conf.confluence.client.http.RestApiInternalClient;
import io.github.md2conf.confluence.client.utils.AssertUtils;
import io.github.md2conf.model.ConfluenceContentModel;

import static io.github.md2conf.confluence.client.PublishConfluenceClientBuilder.aConfluenceClient;

public class ConfluenceClientFactory {

    public static PublishConfluenceClient publishConfluenceClient(ConfluenceClientConfigurationProperties properties,
                                                                  ConfluenceContentModel confluenceContentModel,
                                                                  PublishConfluenceClientListener publishConfluenceClientListener) {
        AssertUtils.assertMandatoryParameter(!confluenceContentModel.getPages().isEmpty(), "Confluence Content Pages");
        ApiInternalClient apiInternalClient = createApiInternalClient(properties);
        PublishConfluenceClientBuilder builder = aConfluenceClient()
                .withConfluenceClientListener(publishConfluenceClientListener)
                .withInternalApiClient(apiInternalClient)
                .withNotifyWatchers(properties.isNotifyWatchers())
                .withOrphanRemovalStrategy(properties.getOrphanRemovalStrategy())
                .withPublishingStrategy(PublishingStrategy.APPEND_TO_ANCESTOR)
                .withVersionMessage(properties.getVersionMessage());

        return builder.build();
    }

    public static ApiInternalClient createApiInternalClient(ConfluenceClientConfigurationProperties properties) {
        return new RestApiInternalClient(properties.getConfluenceUrl(),
                properties.isSkipSslVerification(),
                true,
                properties.getMaxRequestsPerSecond(),
                properties.getConnectionTTL(),
                properties.getUsername(),
                properties.getPasswordOrPersonalAccessToken());
    }
}
