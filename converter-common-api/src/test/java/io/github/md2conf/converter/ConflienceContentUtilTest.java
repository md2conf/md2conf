package io.github.md2conf.converter;

import io.github.md2conf.model.ConfluenceContent;
import io.github.md2conf.model.ConfluencePage;
import io.github.md2conf.model.util.ReadWriteUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.md2conf.model.ConfluenceContent.Type.WIKI;

class ConflienceContentUtilTest {

    @TempDir
    Path outputTmpDir;

    @Test
    void saveConfluenceContentModelToFilesystem() {
        String title = "Single Test Page";

        Map<String, String> attachments = new HashMap<>();
        attachments.put("attachmentOne.txt", absolutePathTo("attachments/attachmentOne.txt"));
        attachments.put("attachmentTwo.txt", absolutePathTo("attachments/attachmentTwo.txt"));

        ConfluencePage confluencePage = createConfluencePageWithAttachments(title, WIKI, absolutePathTo("single-page/single-page.wiki"), attachments);
        ConfluenceContent confluenceContent = new ConfluenceContent(confluencePage);

        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContent.DEFAULT_FILE_NAME)).doesNotExist();
        ReadWriteUtil.saveConfluenceContentModelToFilesystem(confluenceContent, outputTmpDir);

        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContent.DEFAULT_FILE_NAME)).exists();
        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContent.DEFAULT_FILE_NAME)).isNotEmptyFile();
        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContent.DEFAULT_FILE_NAME)).hasExtension("json");
        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContent.DEFAULT_FILE_NAME)).content().contains("single-page/single-page.wiki");
        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContent.DEFAULT_FILE_NAME)).content().contains("attachments/attachmentOne.txt");
        Assertions.assertThat(Path.of(outputTmpDir.toString(), ConfluenceContent.DEFAULT_FILE_NAME)).content().contains("Single Test Page");

    }

    @Test
    void saveToExistingFileIsNotAllowed() {
        Path path =  Paths.get(absolutePathTo("single-page/single-page.wiki"));
        Assertions.assertThat(path).exists();
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            ReadWriteUtil.saveConfluenceContentModelToFilesystem(new ConfluenceContent(), path );
        });
    }

    @Test
    void saveToNonExistingDir() {
        String dir2 = UUID.randomUUID().toString();
        ReadWriteUtil.saveConfluenceContentModelToFilesystem(new ConfluenceContent(), Paths.get(outputTmpDir.toString(), dir2));
        Assertions.assertThat(Path.of(outputTmpDir.toString(), dir2, ConfluenceContent.DEFAULT_FILE_NAME)).exists();
        Assertions.assertThat(Path.of(outputTmpDir.toString(), dir2, ConfluenceContent.DEFAULT_FILE_NAME)).isNotEmptyFile();
    }


    private static String absolutePathTo(String relativePath) {
        return Paths.get("src/test/resources").resolve(relativePath).toAbsolutePath().toString();
    }

    private static ConfluencePage createConfluencePageWithAttachments(String title, ConfluenceContent.Type type, String contentFilePath, Map<String, String> attachments) {
        ConfluencePage confluencePage = new ConfluencePage();
        confluencePage.setTitle(title);
        confluencePage.setType(type);
        confluencePage.setContentFilePath(contentFilePath);
        confluencePage.setAttachments(attachments);
        return confluencePage;
    }



}