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
        ConfluenceContentModel contentModel =  dumpConfluenceClient.dump("ds", "Welcome to Confluence");
        assertThat(contentModel).isNotNull();
        assertThat(contentModel.getPages()).hasSize(1);
        assertThat(contentModel.getPages().get(0).getContentFilePath()).isNotNull();
        Path path = Path.of(contentModel.getPages().get(0).getContentFilePath());
        assertThat(path).isNotEmptyFile();
        assertThat(path.toString()).endsWith(".xhtml");
        assertThat(contentModel.getPages().get(0).getAttachments()).hasSize(1);
        assertThat(contentModel.getPages().get(0).getAttachments()).containsKey("welcome.png");
        assertThat(contentModel.getPages().get(0).getAttachments().get("welcome.png")).endsWith("/welcome.png");
    }
}
