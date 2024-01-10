package io.github.md2conf.converter.view2md;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class FileNameUtilTest {

    @Test
    void sanitize_example() {
        String s = "%/?";
        String res = FileNameUtil.sanitizeFileName(s);
        Assertions.assertThat(res).isEqualTo("%_?");
    }
}