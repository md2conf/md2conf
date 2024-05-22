package io.github.md2conf.converter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.md2conf.converter.AttachmentUtil.copyAttachmentsMap;
import static io.github.md2conf.converter.AttachmentUtil.copyPageAttachments;
import static io.github.md2conf.indexer.PathNameUtils.attachmentsDirectoryByPagePath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchIOException;

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
        Set<Path> copiedAttachments = copyPageAttachments(dstPath, List.of(attachment));
        assertThat(copiedAttachments).hasSize(1).doesNotContainNull();
    }

    @Test
    void copyPageAttachments_will_remove_duplicates() throws IOException {
        Path attachment = Path.of("src/test/resources/sample.txt");
        Path dstPath = tmpDir.resolve("remove_dups.wiki");
        assertThat(dstPath).doesNotExist();
        Set<Path> copiedAttachments = copyPageAttachments(dstPath, List.of(attachment), List.of(attachment) );
        assertThat(copiedAttachments).hasSize(1).doesNotContainNull();
    }

    @Test
    void copyPageAttachments_invoke_twice() throws IOException {
        Path attachment = Path.of("src/test/resources/sample.txt");
        Path dstPath = tmpDir.resolve("1/dstPath_02.wiki");
        assertThat(dstPath).doesNotExist();
        copyPageAttachments(dstPath, List.of(attachment));
        Set<Path> copiedAttachments2 = copyPageAttachments(dstPath, List.of(attachment));
        assertThat(copiedAttachments2).hasSize(1).doesNotContainNull();
    }

    @Test
    void copyAttachmentsMap_will_create_destination_path() throws IOException {
        Path attachment = Path.of("src/test/resources/sample.txt");
        Path dstPath = tmpDir.resolve("2/dstPath_02.wiki");
        assertThat(dstPath).doesNotExist();
        List<Path> copiedAttachments = copyAttachmentsMap(dstPath, Map.of("121", attachment.toString()));
        assertThat(copiedAttachments).hasSize(1);
        assertThat(copiedAttachments.get(0)).exists();
    }

    @Test
    void copyAttachmentsMap_target_dir_is_not_dir() throws IOException {
        Path attachment = Path.of("src/test/resources/sample.txt");
        Path dstPath = tmpDir.resolve("2/dstPath_02.wiki");
        Path dstAttachement = attachmentsDirectoryByPagePath(dstPath);
        dstAttachement.getParent().toFile().mkdirs();
        Assertions.assertThat(dstAttachement.toFile().createNewFile()).isTrue();
        Assertions.assertThat(catchIOException( ()-> copyAttachmentsMap(dstPath, Map.of("121", attachment.toString()))))
                .isNotNull().hasMessageContaining("targetDir is not directory");
    }

    @Test
    void copyAttachmentsMap_invoke_twice() throws IOException {
        Path attachment = Path.of("src/test/resources/sample.txt");
        Path dstPath = tmpDir.resolve("2/dstPath_03.wiki");
        assertThat(dstPath).doesNotExist();
        copyAttachmentsMap(dstPath, Map.of("121", attachment.toString()));
        List<Path> copiedAttachments = copyAttachmentsMap(dstPath, Map.of("121", attachment.toString()));
        assertThat(copiedAttachments).hasSize(1);
        assertThat(copiedAttachments.get(0)).exists();
    }
}