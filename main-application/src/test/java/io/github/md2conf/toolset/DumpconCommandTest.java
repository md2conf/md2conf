package io.github.md2conf.toolset;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

class DumpconCommandTest {

    @Test
    void invoke_no_params() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        int exitCode = cmd.execute("dumpcon");
        Assertions.assertThat(exitCode).isNotZero();
        String errOut = swErr.toString();
        Assertions.assertThat(swOut.toString()).isEmpty();
        Assertions.assertThat(errOut).isNotEmpty().contains("Missing required");
    }

    @Test
    void invoke_with_markdown_format_options() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        int exitCode = cmd.execute("dumpcon","--markdown-right-margin=40");
   //     int exitCode = cmd.execute("dumpcon", "-o="+outputPath,  "-url=http://no_such_host", "--space-key=TMP", "--parent-page-title=tmp","--markdown-right-margin=40");
        Assertions.assertThat(exitCode).isNotZero();
        String errOut = swErr.toString();
        Assertions.assertThat(swOut.toString()).isEmpty();
        Assertions.assertThat(errOut).isNotEmpty().contains("Missing required");
    }

}