package io.github.md2conf.command;


import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

class MainAppTest {


    @Test
    void invoke_main_method_wth_wrong_param() {
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        MainApp.main(new String[]{"wrong param"});
        try {

            assertThat(errContent.toString()).contains("Unmatched argument at index 0");
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
            assertThat(outContent.toString()).contains("Usage: md2conf [-v]");
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

        assertThat(exitCode).isEqualTo(2);
        assertThat(errOut).contains("Set of tools to deal with markdown files and Confluence: publish, dump, convert");
        assertThat(errOut).contains("Usage:");
        assertThat(errOut).contains("Commands:");
        assertThat(errOut).contains("convert");
        assertThat(errOut).contains("conpub");

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
        assertThat(exitCode).isEqualTo(2);
    }
}