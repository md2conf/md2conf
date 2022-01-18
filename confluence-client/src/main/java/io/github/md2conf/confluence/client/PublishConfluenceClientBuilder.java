package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.http.ApiInternalClient;

public final class PublishConfluenceClientBuilder {
    private PublishingStrategy publishingStrategy;
    private OrphanRemovalStrategy orphanRemovalStrategy;
    private ApiInternalClient apiInternalClient;
    private PublishConfluenceClientListener publishConfluenceClientListener;
    private String versionMessage;
    private boolean notifyWatchers;

    private PublishConfluenceClientBuilder() {
    }

    public static PublishConfluenceClientBuilder aConfluenceClient() {
        return new PublishConfluenceClientBuilder();
    }


    public PublishConfluenceClientBuilder withPublishingStrategy(PublishingStrategy publishingStrategy) {
        this.publishingStrategy = publishingStrategy;
        return this;
    }

    public PublishConfluenceClientBuilder withOrphanRemovalStrategy(OrphanRemovalStrategy orphanRemovalStrategy) {
        this.orphanRemovalStrategy = orphanRemovalStrategy;
        return this;
    }

    public PublishConfluenceClientBuilder withInternalApiClient(ApiInternalClient apiInternalClient) {
        this.apiInternalClient = apiInternalClient;
        return this;
    }

    public PublishConfluenceClientBuilder withConfluenceClientListener(PublishConfluenceClientListener publishConfluenceClientListener) {
        this.publishConfluenceClientListener = publishConfluenceClientListener;
        return this;
    }

    public PublishConfluenceClientBuilder withVersionMessage(String versionMessage) {
        this.versionMessage = versionMessage;
        return this;
    }

    public PublishConfluenceClientBuilder withNotifyWatchers(boolean notifyWatchers) {
        this.notifyWatchers = notifyWatchers;
        return this;
    }

    public PublishConfluenceClient build() {
        return new PublishConfluenceClient(publishingStrategy, orphanRemovalStrategy, apiInternalClient, publishConfluenceClientListener, versionMessage, notifyWatchers);
    }
}
