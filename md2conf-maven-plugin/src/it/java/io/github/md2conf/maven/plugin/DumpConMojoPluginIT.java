package io.github.md2conf.maven.plugin;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DumpConMojoPluginIT extends AbstractMd2ConfMojoIT {

    @Test
    void skip() {
        // arrange
        Map<String, String> properties = mandatoryProperties();
        properties.put("skip", "true");
        // act
        var res = invokeGoalAndVerify("dump", "default", properties);
        Path outputPath = res.toPath().resolve("target/md2conf");
        assertThat(outputPath).doesNotExist();
    }

    @Test
    void dump() {
        // arrange
        Map<String, String> properties = mandatoryProperties();
        properties.put("parentPageTitle", "What is Confluence? (step 1 of 9)");
        // act
        var res = invokeGoalAndVerify("dumpcon", "default", properties);
        Path outputPath = res.toPath().resolve("target/md2conf");
        assertThat(outputPath).isNotEmptyDirectory();
        // assert
        assertThat(outputPath.resolve("65552.md")).isRegularFile();
    }


}
