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

    @Test
    void sanitize_long_file_name() {
        String s = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"+
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"+
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.md";
        String res = FileNameUtil.sanitizeFileName(s);
        Assertions.assertThat(res).hasSize(255).endsWith(".md");
    }
}