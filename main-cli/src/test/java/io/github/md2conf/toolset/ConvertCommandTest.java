package io.github.md2conf.toolset;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

class ConvertCommandTest {

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
        cmd.setOut(new PrintWriter(swOut));
        int exitCode = cmd.execute("convert", "--converter=no", "--input-dir="+ emptyDir.toString());
        Assertions.assertThat(exitCode).isZero();
    }


    @Test
    void test_logback_warnings_logged() {
         ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outContent);
        final PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        int exitCode = cmd.execute("convert", "--converter=NO", "--input-dir="+ emptyDir.toString());
        try {
            Assertions.assertThat(exitCode).isZero();
            Assertions.assertThat(outContent.toString()).contains("Output directory is not specified, default is");
        }
        finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void test_invoke_verbose() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        int exitCode = cmd.execute("convert", "--converter=NO", "--input-dir="+ emptyDir.toString(), "-v",  "-o=" + outputPath.toString());
        String errOut = swErr.toString();
        Assertions.assertThat(exitCode).isEqualTo(0);
    }

    @Test
    void test_convert_wiki_page_tree() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        String inputDir = "src/test/resources/wiki_page_tree";
        Assertions.assertThat(outputPath).isEmptyDirectory();
        int exitCode = cmd.execute("convert", "--converter=NO", "--input-dir="+ inputDir, "-o=" + outputPath.toString());
        Assertions.assertThat(exitCode).isEqualTo(0);
        Assertions.assertThat(outputPath).isDirectoryContaining(path -> path.getFileName().toString().equals("confluence-content-model.json"));
    }
}