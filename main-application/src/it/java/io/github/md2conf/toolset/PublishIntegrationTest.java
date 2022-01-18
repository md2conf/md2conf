package io.github.md2conf.toolset;

import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class PublishIntegrationTest extends AbstractContainerTestBase {

    @Test
    void publish_single_page_content() {

        String title = "Example Single Page converted";
        deletePageIfExists(title);
        Assertions.assertThat(super.pageIdBy(title)).isNullOrEmpty();

        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        String modelPath = "src/it/resources/single-page-converted/confluence-content-model.json";
        String[] args = ArrayUtils.addAll(commonPublishArgs(), "-m", modelPath );
        int exitCode = cmd.execute(args);
        Assertions.assertThat(exitCode).isEqualTo(0);
        Assertions.assertThat(super.pageIdBy(title)).isNotNull();

    }

    private String[] commonPublishArgs() {
        String[] args = new String[]{"publish"};
        args = ArrayUtils.addAll(args, CLI_OPTIONS);
        args = ArrayUtils.addAll(args, "-url", confluenceBaseUrl());
        return args;
    }
}