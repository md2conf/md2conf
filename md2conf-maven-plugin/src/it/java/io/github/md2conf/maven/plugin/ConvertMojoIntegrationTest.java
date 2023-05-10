package io.github.md2conf.maven.plugin;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ConvertMojoIntegrationTest extends AbstractMd2ConfMojoIntegrationTest {

    @Test
    void simple_convert() {
        // arrange
        Map<String, String> properties = mandatoryProperties();
        properties.put("inputDirectory", ".");
        // act
        var res = invokeGoalAndVerify("convert", "default", properties);
        Path outputPath = res.toPath().resolve("target/md2conf");
        assertThat(outputPath).isDirectoryContaining(path -> path.getFileName().toString().equals("confluence-content-model.json"));
        assertThat(outputPath).isDirectoryContaining("glob:**/*.wiki");
    }

    @Test
    void skip() {
        // arrange
        Map<String, String> properties = mandatoryProperties();
        properties.put("inputDirectory", ".");
        properties.put("skip", "true");
        // act
        var res = invokeGoalAndVerify("convert", "default", properties);
        Path outputPath = res.toPath().resolve("target/md2conf");
        assertThat(outputPath).doesNotExist();
    }

    @Test
    void no_convert() {
        // arrange
        Map<String, String> properties = mandatoryProperties();
        properties.put("inputDirectory", ".");
        properties.put("converter", "NO");
        // act
        var res = invokeGoalAndVerify("convert", "default", properties);
        Path outputPath = res.toPath().resolve("target/md2conf");
        assertThat(outputPath).isDirectoryContaining(path -> path.getFileName().toString().equals("confluence-content-model.json"));
        assertThat(outputPath).isDirectoryNotContaining("glob:**/*.wiki");
    }


    private static Map<String, String> mandatoryProperties() {
        Map<String, String> properties = new HashMap<>();
        return properties;
    }

}