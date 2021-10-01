package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.http.ApiInternalClient;
import io.github.md2conf.confluence.client.metadata.ConfluenceContentInstance;

public final class ConfluenceClientBuilder {
    private ConfluenceContentInstance metadata;
    private PublishingStrategy publishingStrategy;
    private OrphanRemovalStrategy orphanRemovalStrategy;
    private ApiInternalClient apiInternalClient;
    private ConfluenceClientListener confluenceClientListener;
    private String versionMessage;
    private boolean notifyWatchers;

    private ConfluenceClientBuilder() {
    }

    public static ConfluenceClientBuilder aConfluenceClient() {
        return new ConfluenceClientBuilder();
    }

    public ConfluenceClientBuilder withMetadata(ConfluenceContentInstance metadata) {
        this.metadata = metadata;
        return this;
    }

    public ConfluenceClientBuilder withPublishingStrategy(PublishingStrategy publishingStrategy) {
        this.publishingStrategy = publishingStrategy;
        return this;
    }

    public ConfluenceClientBuilder withOrphanRemovalStrategy(OrphanRemovalStrategy orphanRemovalStrategy) {
        this.orphanRemovalStrategy = orphanRemovalStrategy;
        return this;
    }

    public ConfluenceClientBuilder withInternalApiClient(ApiInternalClient apiInternalClient) {
        this.apiInternalClient = apiInternalClient;
        return this;
    }

    public ConfluenceClientBuilder withConfluenceClientListener(ConfluenceClientListener confluenceClientListener) {
        this.confluenceClientListener = confluenceClientListener;
        return this;
    }

    public ConfluenceClientBuilder withVersionMessage(String versionMessage) {
        this.versionMessage = versionMessage;
        return this;
    }

    public ConfluenceClientBuilder withNotifyWatchers(boolean notifyWatchers) {
        this.notifyWatchers = notifyWatchers;
        return this;
    }

    public ConfluenceClient build() {
        return new ConfluenceClient(metadata, publishingStrategy, orphanRemovalStrategy, apiInternalClient, confluenceClientListener, versionMessage, notifyWatchers);
    }
}
