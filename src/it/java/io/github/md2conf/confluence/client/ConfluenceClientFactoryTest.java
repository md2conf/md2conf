package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.http.RequestFailedException;
import io.github.md2conf.model.ConfluenceContent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class ConfluenceClientFactoryTest extends AbstractContainerBaseTest {

    @Test
    void test_create_client_with_not_existing_space() {
        ConfluenceClientConfigurationProperties properties = aDefaultConfluenceClientConfigurationProperties()
                .withSpaceKey("no-such-space")
                .build();
        RequestFailedException requestFailedException =  Assertions.assertThrows(RequestFailedException.class, () -> {
            ConfluenceClientFactory.confluenceClient(properties, new ConfluenceContent(), null );
        });
        Assertions.assertTrue(requestFailedException.getMessage().contains("No space with key"));
    }

    @Test
    void test_create_client_with_not_existing_parent_page() {
        ConfluenceClientConfigurationProperties properties = aDefaultConfluenceClientConfigurationProperties()
                .withParentPageTitle("page" + UUID.randomUUID())
                .build();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ConfluenceClientFactory.confluenceClient(properties, new ConfluenceContent(), null );
        });

        Assertions.assertTrue(exception.getMessage().contains("There is no page with title"));
    }

    @Test
    void test_create_client_with_existing_space_and_parent_page() {
        ConfluenceClientConfigurationProperties properties = aDefaultConfluenceClientConfigurationProperties()
                .build();
        ConfluenceClient confluenceClient = ConfluenceClientFactory.confluenceClient(properties, new ConfluenceContent(), null );
        Assertions.assertNotNull(confluenceClient);
    }
}