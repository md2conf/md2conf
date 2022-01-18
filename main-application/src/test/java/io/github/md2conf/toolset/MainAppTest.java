package io.github.md2conf.toolset;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

class MainAppTest {


    @Test
    void invoke_main_method_wth_wrong_param() {
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        MainApp.main(new String[]{"wrong param"});
        try {

            Assertions.assertThat(errContent.toString()).contains("Unmatched argument at index 0");
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void invoke_main_method_help() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        MainApp.main(new String[]{"help"});
        try {
            Assertions.assertThat(outContent.toString()).contains("Usage: md2conf [-v]");
        } finally {
            System.setOut(originalOut);
        }
    }

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
        Assertions.assertThat(errOut).contains("md2conf help model");

    }

    @Test
    void test_invoke_verbose() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        int exitCode = cmd.execute("--verbose");
        String errOut = swErr.toString();
        Assertions.assertThat(exitCode).isEqualTo(2);
    }
}