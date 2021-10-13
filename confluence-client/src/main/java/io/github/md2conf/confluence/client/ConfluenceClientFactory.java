package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.http.ApiInternalClient;
import io.github.md2conf.confluence.client.http.NotFoundException;
import io.github.md2conf.confluence.client.http.RestApiInternalClient;
import io.github.md2conf.confluence.client.metadata.ConfluenceContentInstance;
import io.github.md2conf.confluence.client.utils.AssertUtils;
import io.github.md2conf.model.ConfluenceContentModel;

import static io.github.md2conf.confluence.client.ConfluenceClientBuilder.aConfluenceClient;

public class ConfluenceClientFactory {

    public static ConfluenceClient confluenceClient(ConfluenceClientConfigurationProperties properties,
                                                    ConfluenceContentModel confluenceContentModel,
                                                    ConfluenceClientListener confluenceClientListener) {
        AssertUtils.assertMandatoryParameter(!confluenceContentModel.getPages().isEmpty(), "Confluence Content Pages");
        ApiInternalClient apiInternalClient = new RestApiInternalClient(properties.getConfluenceUrl(),
                properties.isSkipSslVerification(),
                true,
                properties.getMaxRequestsPerSecond(),
                properties.getUsername(),
                properties.getPasswordOrPersonalAccessToken());

        String ancestorId;
        try {
            ancestorId=  apiInternalClient.getPageByTitle(properties.getSpaceKey(), properties.getParentPageTitle());
        } catch (NotFoundException e){
            throw new IllegalArgumentException(String.format("Cannot create ConfluenceClient. There is no page with title %s in %s space found",
                    properties.getParentPageTitle(), properties.getSpaceKey()));
        }

        ConfluenceContentInstance metadata = new ConfluenceContentInstance();
        metadata.setSpaceKey(properties.getSpaceKey());
        metadata.setAncestorId(ancestorId);
        metadata.setPages(confluenceContentModel.getPages());

        ConfluenceClientBuilder builder = aConfluenceClient()
                .withConfluenceClientListener(confluenceClientListener)
                .withInternalApiClient(apiInternalClient)
                .withMetadata(metadata)
                .withNotifyWatchers(properties.isNotifyWatchers())
                .withOrphanRemovalStrategy(properties.getOrphanRemovalStrategy())
                .withPublishingStrategy(PublishingStrategy.APPEND_TO_ANCESTOR)
                .withVersionMessage(properties.getVersionMessage());

        return builder.build();
    }
}
