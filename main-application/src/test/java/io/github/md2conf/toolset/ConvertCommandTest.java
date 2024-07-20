package io.github.md2conf.toolset;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(exitCode).isNotZero();
        String errOut = swErr.toString();
        assertThat(swOut.toString()).isEmpty();
        assertThat(errOut).contains("Commands")
                .doesNotContain("publish").doesNotContain("Exception");
    }

    @Test
    @Disabled
    void invoke_no_converter_empty_dir() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        cmd.setCaseInsensitiveEnumValuesAllowed(true);
        StringWriter swOut = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        int exitCode = cmd.execute("convert", "--converter=no", "--input-dir="+ emptyDir);
        assertThat(exitCode).isZero();
    }


    @Test
    @Disabled
    void test_logback_warnings_logged() {
         ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outContent);
        final PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        int exitCode = cmd.execute("convert", "--converter=NO", "--input-dir="+ emptyDir);
        try {
            assertThat(exitCode).isZero();
            assertThat(outContent.toString()).contains("Output directory is not specified, default is");
        }
        finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @Disabled //todo fix
    void test_invoke_verbose() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        int exitCode = cmd.execute("convert", "--converter=NO", "--input-dir="+ emptyDir, "-v",  "-o=" + outputPath);
        String errOut = swErr.toString();
        assertThat(exitCode).isEqualTo(0);
        assertThat(errOut).doesNotContain("publish");
    }




    @Test
    @Disabled
    void invoke_view2md_converter() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        String inputDir = "src/test/resources/view_single_page";
        assertThat(outputPath).isEmptyDirectory();
        int exitCode = cmd.execute("convert", "--converter=VIEW2MD", "--input-dir=" + inputDir, "-o=" + outputPath);
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputPath.resolve("Welcome to Confluence.md")).isRegularFile().content().contains("Share your page with a team member");
        assertThat(outputPath.resolve("Welcome to Confluence_attachments/welcome.png")).isRegularFile().content();
    }

    @Test
    @Disabled
    void invoke_view2md_converter_with_format_options() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        String inputDir = "src/test/resources/view_single_page";
        assertThat(outputPath).isEmptyDirectory();
        int exitCode = cmd.execute("convert", "--converter=VIEW2MD", "--input-dir=" + inputDir, "-o=" + outputPath, "--markdown-right-margin=40");
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputPath.resolve("Welcome to Confluence.md")).isRegularFile().content().contains("Share your page with a team member");
        assertThat(outputPath.resolve("Welcome to Confluence_attachments/welcome.png")).isRegularFile().content();
    }
}