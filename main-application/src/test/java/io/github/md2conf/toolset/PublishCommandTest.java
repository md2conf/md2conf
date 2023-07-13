package io.github.md2conf.toolset;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

class PublishCommandTest {

    @Test
    void invoke_no_params() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        int exitCode = cmd.execute("publish");
        assertThat(exitCode).isNotZero();
        String errOut = swErr.toString();
        assertThat(swOut.toString()).isEmpty();
        assertThat(errOut).isNotEmpty();
        assertThat(errOut).doesNotContain("convert").doesNotContain("Exception").contains("Missing required argument");
    }


    @Test
    void invoke_with_path_to_existing_model_and_non_reachable_server() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        String model = "src/test/resources/single-page-converted/confluence-content-model.json";
        int exitCode = cmd.execute("publish", "-m", model, "-url", "http://localhost:6551", "-s", "TEST", "-pt", "Test" );
        assertThat(exitCode).isNotZero();
        String errOut = swErr.toString();
        assertThat(swOut.toString()).isEmpty();
        assertThat(errOut).isNotEmpty();
        assertThat(errOut).contains("Connection refused").doesNotContain("Convert");
    }

    @Test
    void invoke_with_path_to_existing_model_directory_and_non_reachable_server() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        String model = "src/test/resources/single-page-converted";
        int exitCode = cmd.execute("publish", "-m", model, "-url", "http://localhost:6551", "-s", "TEST", "-pt", "Test" );
        assertThat(exitCode).isNotZero();
        String errOut = swErr.toString();
        assertThat(swOut.toString()).isEmpty();
        assertThat(errOut).isNotEmpty();
        assertThat(errOut).contains("Connection refused").doesNotContain("Convert");
    }

    @Test
    void invoke_no_existsing_model() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        int exitCode = cmd.execute("publish", "-m", "null.json", "-url", "http://localhost", "-s", "TEST", "-pt", "Test" );
        assertThat(exitCode).isNotZero();
        String errOut = swErr.toString();
        assertThat(swOut.toString()).isEmpty();
        assertThat(errOut).isNotEmpty();
        assertThat(errOut).doesNotContain("convert");
    }
}