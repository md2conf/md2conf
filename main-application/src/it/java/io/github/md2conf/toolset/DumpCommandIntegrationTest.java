package io.github.md2conf.toolset;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class DumpCommandIntegrationTest extends AbstractContainerTestBase {

    @TempDir
    private Path outputPath;

    @Test
    void dump_demo_space() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        String[] args = ArrayUtils.addAll(commonPublishArgs(), "-o", outputPath.toString() );
        int exitCode = cmd.execute(args);
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputPath).isDirectoryContaining("glob:**.xhtml");
        assertThat(outputPath).isDirectoryContaining("glob:**.png");
        assertThat(outputPath.resolve("confluence-content-model.json")).isNotEmptyFile();
    }

    private String[] commonPublishArgs() {
        String[] args = new String[]{"dump"};
        args = ArrayUtils.addAll(args, CLI_OPTIONS);
        args = ArrayUtils.addAll(args, "-url", confluenceBaseUrl());
        return args;
    }
}
