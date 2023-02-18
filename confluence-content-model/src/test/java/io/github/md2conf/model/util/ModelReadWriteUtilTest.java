package io.github.md2conf.model.util;

import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.md2conf.model.ConfluenceContentModel.Type.WIKI;
import static io.github.md2conf.model.util.ModelReadWriteUtil.readFromYamlOrJson;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelReadWriteUtilTest {

    private static final String TEST_RESOURCES = "src/test/resources";

    @TempDir
    Path outputTmpDir;

    @Test
    void save_confluence_content_model_to_filesystem() {
        String title = "Single Test Page";

        Map<String, String> attachments = new HashMap<>();
        attachments.put("attachmentOne.txt", absolutePathTo("attachments/attachmentOne.txt"));
        attachments.put("attachmentTwo.txt", absolutePathTo("attachments/attachmentTwo.txt"));

        ConfluencePage confluencePage = createConfluencePageWithAttachments(title, WIKI, absolutePathTo("single-page/single-page.wiki"), attachments);
        ConfluenceContentModel confluenceContentModel = new ConfluenceContentModel(confluencePage);

        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContentModel.DEFAULT_FILE_NAME)).doesNotExist();
        ModelReadWriteUtil.saveConfluenceContentModelToFilesystem(confluenceContentModel, outputTmpDir);

        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContentModel.DEFAULT_FILE_NAME)).exists();
        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContentModel.DEFAULT_FILE_NAME)).isNotEmptyFile();
        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContentModel.DEFAULT_FILE_NAME)).hasExtension("json");
        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContentModel.DEFAULT_FILE_NAME)).content().contains("single-page/single-page.wiki");
        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContentModel.DEFAULT_FILE_NAME)).content().contains("attachments/attachmentOne.txt");
        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContentModel.DEFAULT_FILE_NAME)).content().contains("Single Test Page");

    }

    @Test
    void save_to_existing_file_is_not_allowed() {
        Path path =  Paths.get(absolutePathTo("single-page/single-page.wiki"));
        Assertions.assertThat(path).exists();
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            ModelReadWriteUtil.saveConfluenceContentModelToFilesystem(new ConfluenceContentModel(), path );
        });
    }

    @Test
    void save_to_non_existing_dir() {
        String dir2 = UUID.randomUUID().toString();
        ModelReadWriteUtil.saveConfluenceContentModelToFilesystem(new ConfluenceContentModel(), Paths.get(outputTmpDir.toString(), dir2));
        Assertions.assertThat(Path.of(outputTmpDir.toString(), dir2, ConfluenceContentModel.DEFAULT_FILE_NAME)).exists();
        Assertions.assertThat(Path.of(outputTmpDir.toString(), dir2, ConfluenceContentModel.DEFAULT_FILE_NAME)).isNotEmptyFile();
    }


    private static String absolutePathTo(String relativePath) {
        return Paths.get(TEST_RESOURCES).resolve(relativePath).toAbsolutePath().toString();
    }

    private static ConfluencePage createConfluencePageWithAttachments(String title, ConfluenceContentModel.Type type, String contentFilePath, Map<String, String> attachments) {
        ConfluencePage confluencePage = new ConfluencePage();
        confluencePage.setTitle(title);
        confluencePage.setType(type);
        confluencePage.setContentFilePath(contentFilePath);
        confluencePage.setAttachments(attachments);
        return confluencePage;
    }

    @Test
    void test_read_confluence_content_zero_page() throws IOException {
        File file = Paths.get(TEST_RESOURCES, "confluence-content-zero-page.json").toFile();
        ConfluenceContentModel confluenceContentModel =  readFromYamlOrJson(file);
        assertNotNull(confluenceContentModel);
        assertTrue(confluenceContentModel.getPages().isEmpty());
    }

    @Test
    void test_read_confluence_content_confluence_content_non_model_fields() throws IOException {
        File file = Paths.get(TEST_RESOURCES, "confluence-content-non-model-fields.json").toFile();
        ConfluenceContentModel confluenceContentModel =  readFromYamlOrJson(file);
        assertNotNull(confluenceContentModel);
        assertFalse(confluenceContentModel.getPages().isEmpty());
    }


    @Test
    void test_read_bad_json() {
        File file = Paths.get(TEST_RESOURCES, "bad-json.json").toFile();
        Throwable exception = assertThrows(RuntimeException.class,
                ()-> readFromYamlOrJson(file));
        Assertions.assertThat(exception).hasCauseInstanceOf(IOException.class);
    }
}