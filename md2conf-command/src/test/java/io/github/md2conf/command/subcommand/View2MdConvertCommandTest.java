package io.github.md2conf.command.subcommand;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.StringWriter;
import java.nio.file.Path;

import static io.github.md2conf.command.TestUtil.getCommandLine;
import static org.assertj.core.api.Assertions.assertThat;

class View2MdConvertCommandTest {
    @TempDir
    private Path emptyDir;

    @TempDir
    private Path outputPath;

    @Test
    void when_invokeNoParams_then_missingRequiredArgumentPrinted() {
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        CommandLine cmd = getCommandLine(swOut, swErr);
        int exitCode = cmd.execute("convert", "view2md");
        assertThat(exitCode).isNotZero();
        assertThat(swOut.toString()).isEmpty();
        assertThat(swErr.toString()).isNotEmpty().doesNotContain("Exception")
                .contains("Usage: md2conf")
                .contains("Missing required argument")
                .contains("Error: Missing required argument(s): (-o=<outputDirectory> --model-path=<modelPath>");;
    }


}