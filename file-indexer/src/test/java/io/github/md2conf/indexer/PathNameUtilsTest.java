package io.github.md2conf.indexer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.catchIllegalArgumentException;

class PathNameUtilsTest {
    @Test
    void attachmentsDirectoryByPagePath() {
        Path res = PathNameUtils.attachmentsDirectoryByPagePath(Path.of("/tmp/1.txt"));
        Assertions.assertThat(res.toString()).isEqualTo("/tmp/1"+PathNameUtils.ATTACHMENTS_SUFFIX);
    }

    @Test
    void removeExtension() {
        Path path  = PathNameUtils.removeExtension(Path.of("/tmp/1.txt"));
        Assertions.assertThat(path.toString()).isEqualTo("/tmp/1");
    }

    @Test
    void removeExtension_error_in_case_of_no_extension() {
        IllegalArgumentException e = catchIllegalArgumentException(()->PathNameUtils.removeExtension(Path.of("/tmp/1")));
        Assertions.assertThat(e).hasMessageContaining("Expected path with extension");
    }
}