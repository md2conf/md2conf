package io.github.md2conf.toolset;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

class ConvertCommandTest {

    @TempDir
    private Path emptyDir;


    @Test
    void invoke_no_params() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        int exitCode = cmd.execute("convert");
        Assertions.assertThat(exitCode).isNotZero();
        String errOut = swErr.toString();
        Assertions.assertThat(swOut.toString()).isEmpty();
        Assertions.assertThat(errOut).isNotEmpty();
    }

    @Test
    void invoke_no_converter() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        cmd.setCaseInsensitiveEnumValuesAllowed(true);
        StringWriter swOut = new StringWriter();
        int exitCode = cmd.execute("convert", "--converter=no", "--input-dir="+ emptyDir.toString());
        Assertions.assertThat(exitCode).isZero();
    }
}