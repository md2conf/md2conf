package io.github.md2conf.model.util;

import io.github.md2conf.model.ConfluenceContent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ModelReaderUtilTest {

    private static final String TEST_RESOURCES = "src/test/resources";

    @Test
    void test_read_confluence_content_zero_page() throws IOException {
        File file = Paths.get(TEST_RESOURCES, "confluence-content-zero-page.json").toFile();
        ConfluenceContent confluenceContent =  ModelReaderUtil.readFromYamlOrJson(file);
        assertNotNull(confluenceContent);
        assertTrue(confluenceContent.getPages().isEmpty());
    }

    @Test
    void test_read_confluence_content_confluence_content_non_model_fields() throws IOException {
        File file = Paths.get(TEST_RESOURCES, "confluence-content-non-model-fields.json").toFile();
        ConfluenceContent confluenceContent =  ModelReaderUtil.readFromYamlOrJson(file);
        assertNotNull(confluenceContent);
        assertFalse(confluenceContent.getPages().isEmpty());
    }

    @Test
    void test_read_confluence_client_sample_config_yaml() throws IOException {
        File file = Paths.get(TEST_RESOURCES, "confluence-client-sample-config.yaml").toFile();
        ConfluenceContent confluenceContent =  ModelReaderUtil.readFromYamlOrJson(file);
        assertNotNull(confluenceContent);
        assertFalse(confluenceContent.getPages().isEmpty());
    }

    @Test
    void test_read_confluence_client_sample_config_json() throws IOException {
        File file = Paths.get(TEST_RESOURCES, "confluence-client-sample-config.yaml").toFile();
        ConfluenceContent confluenceContent =  ModelReaderUtil.readFromYamlOrJson(file);
        assertNotNull(confluenceContent);
        assertFalse(confluenceContent.getPages().isEmpty());
    }
}