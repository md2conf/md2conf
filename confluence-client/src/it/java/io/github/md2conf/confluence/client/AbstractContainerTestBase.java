package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.ConfluenceClientConfigurationProperties.ConfluenceClientConfigurationPropertiesBuilder;

import static io.github.md2conf.confluence.client.ConfluenceClientConfigurationProperties.ConfluenceClientConfigurationPropertiesBuilder.aConfluenceClientConfigurationProperties;

public class AbstractContainerTestBase {


    String confluenceBaseUrl(){
        return "http://localhost:8090";
    }

    static String PARENT_PAGE_TITLE = "Welcome to Confluence";
    static String SPACE_KEY = "ds";

    ConfluenceClientConfigurationPropertiesBuilder aDefaultConfluenceClientConfigurationProperties(){
        return aConfluenceClientConfigurationProperties()
                .withConfluenceUrl(confluenceBaseUrl())
                .withUsername("admin")
                .withPasswordOrPersonalAccessToken("admin")
                .withSpaceKey(SPACE_KEY)
                .withParentPageTitle(PARENT_PAGE_TITLE)
                .withConnectionTTL(500);
    }


}
