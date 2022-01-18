package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.http.ApiInternalClient;
import io.github.md2conf.confluence.client.http.RequestFailedException;
import io.github.md2conf.model.ConfluenceContentModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class DumpConfluenceClientIntegrationTest extends AbstractContainerTestBase {

    @TempDir
    private Path tmpDir;

    @Test
    void dump_from_non_existing_space() throws IOException {
        ConfluenceClientConfigurationProperties properties = aDefaultConfluenceClientConfigurationProperties().build();
        ApiInternalClient internalClient = ConfluenceClientFactory.createApiInternalClient(properties);
        DumpConfluenceClient dumpConfluenceClient = new DumpConfluenceClient(internalClient, tmpDir);
        RequestFailedException requestFailedException =  Assertions.assertThrows(RequestFailedException.class, () -> {
            dumpConfluenceClient.dump("no_such_space", "no_such_page");
        });
        Assertions.assertTrue(requestFailedException.getMessage().contains("No space with key"));

    }

    @Test
    void dump_single_page_from_demo_space() throws IOException {
        ConfluenceClientConfigurationProperties properties = aDefaultConfluenceClientConfigurationProperties().build();
        ApiInternalClient internalClient = ConfluenceClientFactory.createApiInternalClient(properties);
        DumpConfluenceClient dumpConfluenceClient = new DumpConfluenceClient(internalClient, tmpDir);
        ConfluenceContentModel confluenceContentInstance =  dumpConfluenceClient.dump("ds", "What is Confluence? (step 1 of 9)");
        assertThat(confluenceContentInstance).isNotNull();
        assertThat(confluenceContentInstance.getPages()).hasSize(1);
        assertThat(confluenceContentInstance.getPages().get(0).getContentFilePath()).isNotNull();
        Path path = Path.of(confluenceContentInstance.getPages().get(0).getContentFilePath());
        assertThat(path).isNotEmptyFile();
    }
}
