package io.github.md2conf.maven.plugin;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConPubMojoIntegrationTest extends AbstractMd2ConfMojoIT {


    @Test
    void conpub() {
        // arrange
        Map<String, String> properties = mandatoryProperties();
        properties.put("inputDirectory", ".");
        // act
        var res = invokeGoalAndVerify("conpub", "default", properties);
        Path outputPath = res.toPath().resolve("target/md2conf");
        assertThat(outputPath).exists();
        assertThat(outputPath.resolve("confluence-content-model.json")).exists().isRegularFile();
    }


    @Test
    void skip() {
        // arrange
        Map<String, String> properties = mandatoryProperties();
        properties.put("skip", "true");
        // act
        var res = invokeGoalAndVerify("conpub", "default", properties);
        Path outputPath = res.toPath().resolve("target/md2conf");
        assertThat(outputPath).doesNotExist();
    }


}
