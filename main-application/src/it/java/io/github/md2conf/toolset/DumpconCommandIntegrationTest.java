package io.github.md2conf.toolset;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class DumpconCommandIntegrationTest extends AbstractContainerTestBase {

    @TempDir
    private Path outputPath;

    @Test
    void dumpcon_demo_space() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        String[] args = ArrayUtils.addAll(commonConfluenceArgs(), "-o", outputPath.toString() );
        int exitCode = cmd.execute(args);
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputPath).isDirectoryNotContaining("glob:**.xhtml");
        assertThat(outputPath.resolve("65551.md")).isRegularFile();
        assertThat(outputPath.resolve("65551_attachments")).isDirectoryContaining("glob:**.png");
        assertThat(outputPath.resolve("65551")).isDirectoryContaining("glob:**.md");
    }

    private String[] commonConfluenceArgs() {
        String[] args = new String[]{"dumpcon"};
        args = ArrayUtils.addAll(args, CLI_OPTIONS);
        args = ArrayUtils.addAll(args, "-url", confluenceBaseUrl());
        return args;
    }
}
