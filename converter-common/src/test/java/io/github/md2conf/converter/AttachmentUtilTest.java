package io.github.md2conf.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static io.github.md2conf.converter.AttachmentUtil.copyPageAttachments;
import static org.assertj.core.api.Assertions.assertThat;

class AttachmentUtilTest {

    @TempDir
    private Path tmpDir;

    @Test
    void toAttachmentsMap() {
        List<Path> list = List.of(Path.of("tmp/1.txt"), Path.of("2.txt"));
        Map<String, String> res = AttachmentUtil.toAttachmentsMap(list);
        assertThat(res).hasSize(2);
        assertThat(res.get("1.txt")).isEqualTo("tmp/1.txt");
        assertThat(res.get("2.txt")).isEqualTo("2.txt");

    }

    @Test
    void copyPageAttachments_will_create_destination_path() throws IOException {
        Path attachment = Path.of("src/test/resources/sample.txt");
        Path dstPath = tmpDir.resolve("1/dstPath_01.wiki");
        assertThat(dstPath).doesNotExist();
        List<Path> copiedAttachments = copyPageAttachments(dstPath, List.of(attachment));
        assertThat(copiedAttachments).hasSize(1);
        assertThat(copiedAttachments.get(0)).exists();
    }

    @Test
    void copyPageAttachments_invoke_twice() throws IOException {
        Path attachment = Path.of("src/test/resources/sample.txt");
        Path dstPath = tmpDir.resolve("1/dstPath_02.wiki");
        assertThat(dstPath).doesNotExist();
        copyPageAttachments(dstPath, List.of(attachment));
        List<Path> copiedAttachments2 = copyPageAttachments(dstPath, List.of(attachment));
        assertThat(copiedAttachments2).hasSize(1);
        assertThat(copiedAttachments2.get(0)).exists();
    }
}