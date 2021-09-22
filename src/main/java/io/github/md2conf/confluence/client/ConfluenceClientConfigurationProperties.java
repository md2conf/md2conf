package io.github.md2conf.confluence.client;

public class ConfluenceClientConfigurationProperties {

    private String confluenceUrl;
    private String username;
    private String passwordOrPersonalAccessToken;
    private String spaceKey;
    private String parentPageTitle;
    private String versionMessage = "Published by md2conf";
    private OrphanRemovalStrategy orphanRemovalStrategy;
    private boolean notifyWatchers = false;
    private boolean skipSslVerification = false;
    private Double maxRequestsPerSecond;

    public String getConfluenceUrl() {
        return confluenceUrl;
    }

    public void setConfluenceUrl(String confluenceUrl) {
        this.confluenceUrl = confluenceUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordOrPersonalAccessToken() {
        return passwordOrPersonalAccessToken;
    }

    public void setPasswordOrPersonalAccessToken(String passwordOrPersonalAccessToken) {
        this.passwordOrPersonalAccessToken = passwordOrPersonalAccessToken;
    }

    public String getSpaceKey() {
        return spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getParentPageTitle() {
        return parentPageTitle;
    }

    public void setParentPageTitle(String parentPageTitle) {
        this.parentPageTitle = parentPageTitle;
    }

    public String getVersionMessage() {
        return versionMessage;
    }

    public void setVersionMessage(String versionMessage) {
        this.versionMessage = versionMessage;
    }

    public OrphanRemovalStrategy getOrphanRemovalStrategy() {
        return orphanRemovalStrategy;
    }

    public void setOrphanRemovalStrategy(OrphanRemovalStrategy orphanRemovalStrategy) {
        this.orphanRemovalStrategy = orphanRemovalStrategy;
    }

    public boolean isNotifyWatchers() {
        return notifyWatchers;
    }

    public void setNotifyWatchers(boolean notifyWatchers) {
        this.notifyWatchers = notifyWatchers;
    }

    public boolean isSkipSslVerification() {
        return skipSslVerification;
    }

    public void setSkipSslVerification(boolean skipSslVerification) {
        this.skipSslVerification = skipSslVerification;
    }

    public Double getMaxRequestsPerSecond() {
        return maxRequestsPerSecond;
    }

    public void setMaxRequestsPerSecond(Double maxRequestsPerSecond) {
        this.maxRequestsPerSecond = maxRequestsPerSecond;
    }


    public static final class ConfluenceClientConfigurationPropertiesBuilder {
        private String confluenceUrl;
        private String username;
        private String passwordOrPersonalAccessToken;
        private String spaceKey;
        private String parentPageTitle;
        private String versionMessage = "Published by md2conf";
        private OrphanRemovalStrategy orphanRemovalStrategy;
        private boolean notifyWatchers = false;
        private boolean skipSslVerification = false;
        private Double maxRequestsPerSecond;

        private ConfluenceClientConfigurationPropertiesBuilder() {
        }

        public static ConfluenceClientConfigurationPropertiesBuilder aConfluenceClientConfigurationProperties() {
            return new ConfluenceClientConfigurationPropertiesBuilder();
        }

        public ConfluenceClientConfigurationPropertiesBuilder withConfluenceUrl(String confluenceUrl) {
            this.confluenceUrl = confluenceUrl;
            return this;
        }

        public ConfluenceClientConfigurationPropertiesBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public ConfluenceClientConfigurationPropertiesBuilder withPasswordOrPersonalAccessToken(String passwordOrPersonalAccessToken) {
            this.passwordOrPersonalAccessToken = passwordOrPersonalAccessToken;
            return this;
        }

        public ConfluenceClientConfigurationPropertiesBuilder withSpaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
            return this;
        }

        public ConfluenceClientConfigurationPropertiesBuilder withParentPageTitle(String parentPageTitle) {
            this.parentPageTitle = parentPageTitle;
            return this;
        }

        public ConfluenceClientConfigurationPropertiesBuilder withVersionMessage(String versionMessage) {
            this.versionMessage = versionMessage;
            return this;
        }

        public ConfluenceClientConfigurationPropertiesBuilder withOrphanRemovalStrategy(OrphanRemovalStrategy orphanRemovalStrategy) {
            this.orphanRemovalStrategy = orphanRemovalStrategy;
            return this;
        }

        public ConfluenceClientConfigurationPropertiesBuilder withNotifyWatchers(boolean notifyWatchers) {
            this.notifyWatchers = notifyWatchers;
            return this;
        }

        public ConfluenceClientConfigurationPropertiesBuilder withSkipSslVerification(boolean skipSslVerification) {
            this.skipSslVerification = skipSslVerification;
            return this;
        }

        public ConfluenceClientConfigurationPropertiesBuilder withMaxRequestsPerSecond(Double maxRequestsPerSecond) {
            this.maxRequestsPerSecond = maxRequestsPerSecond;
            return this;
        }

        public ConfluenceClientConfigurationProperties build() {
            ConfluenceClientConfigurationProperties confluenceClientConfigurationProperties = new ConfluenceClientConfigurationProperties();
            confluenceClientConfigurationProperties.setConfluenceUrl(confluenceUrl);
            confluenceClientConfigurationProperties.setUsername(username);
            confluenceClientConfigurationProperties.setPasswordOrPersonalAccessToken(passwordOrPersonalAccessToken);
            confluenceClientConfigurationProperties.setSpaceKey(spaceKey);
            confluenceClientConfigurationProperties.setParentPageTitle(parentPageTitle);
            confluenceClientConfigurationProperties.setVersionMessage(versionMessage);
            confluenceClientConfigurationProperties.setOrphanRemovalStrategy(orphanRemovalStrategy);
            confluenceClientConfigurationProperties.setNotifyWatchers(notifyWatchers);
            confluenceClientConfigurationProperties.setSkipSslVerification(skipSslVerification);
            confluenceClientConfigurationProperties.setMaxRequestsPerSecond(maxRequestsPerSecond);
            return confluenceClientConfigurationProperties;
        }
    }
}
