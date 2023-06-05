package io.github.md2conf.maven.plugin;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;

public class PublishMojoPluginIT extends AbstractMd2ConfMojoIT {

    @Test
    void skip() {
        // arrange
        Map<String, String> properties = mandatoryProperties();
        properties.put("skip", "true");
        // act
        var res = invokeGoalAndVerify("publish", "default", properties);
        Path outputPath = res.toPath().resolve("target/md2conf");
        assertThat(outputPath).doesNotExist();
    }

    @Test
    void publish() {
        // arrange
        Map<String, String> properties = mandatoryProperties();
        properties.put("confluenceContentModelPath", "./confluence-content-model.json");
        // act
        var res = invokeGoalAndVerify("publish", "publish", properties);
        Path outputPath = res.toPath().resolve("target/md2conf");
        assertThat(outputPath).doesNotExist();
        // assert
        givenAuthenticatedAsPublisher()
                .when().get(childPages())
                .then().body("results.title", hasItem("Example publish from maven"));
    }


}
