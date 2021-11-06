package io.github.md2conf.toolset;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

class MainAppTest {

    @Test
    void invoke_without_parameters() {

        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));

        int exitCode = cmd.execute();
        String errOut = swErr.toString();

        Assertions.assertThat(exitCode).isEqualTo(2);
        Assertions.assertThat(errOut).contains("Set of tools to work with 'confluence-content-model': publish, dump, convert.");
        Assertions.assertThat(errOut).contains("Usage:");
        Assertions.assertThat(errOut).contains("Commands:");
        Assertions.assertThat(errOut).contains("convert");
        Assertions.assertThat(errOut).contains("conpub");
        Assertions.assertThat(errOut).contains("md2conf help md2conf");

    }
}