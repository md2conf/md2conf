package io.github.md2conf.toolset.subcommand;

import io.github.md2conf.toolset.MainApp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class View2MdConvertCommandTest {
    @TempDir
    private Path emptyDir;

    @TempDir
    private Path outputPath;

    @Test
    void invoke_no_params() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        int exitCode = cmd.execute("convert", "view2md");
        assertThat(exitCode).isNotZero();
        String errOut = swErr.toString();
        assertThat(swOut.toString()).isEmpty();
        assertThat(errOut).isNotEmpty().contains("Error: Missing required argument(s): (-o=<outputDirectory> --model-path=<modelPath>");
        assertThat(errOut).doesNotContain("publish").doesNotContain("Exception").contains("Missing required argument");
    }

}