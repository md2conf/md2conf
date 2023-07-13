package io.github.md2conf.toolset;

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
        assertThat(errOut).isNotEmpty().contains("Missing required argument");
        assertThat(errOut).doesNotContain("publish").doesNotContain("Exception").contains("Missing required argument");
    }

    @Test
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
    void convert_non_existing_dir() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        int exitCode = cmd.execute("convert", "--converter=NO", "--input-dir="+ outputPath + "/non_exists", "-v",  "-o=" + outputPath);
        String errOut = swErr.toString();
        assertThat(exitCode).isNotZero();
        assertThat(errOut).contains("NoSuchFileException");
    }

    @Test
    void invoke_no_converter_dir_with_wiki_page_tree() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        String inputDir = "src/test/resources/wiki_page_tree";
        assertThat(outputPath).isEmptyDirectory();
        int exitCode = cmd.execute("convert", "--converter=NO", "--input-dir="+ inputDir, "-o=" + outputPath);
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputPath).isDirectoryContaining(path -> path.getFileName().toString().equals("confluence-content-model.json"));
        assertThat(outputPath).isDirectoryNotContaining("glob:**/*.wiki");
    }

    @Test
    void invoke_copying_converter_dir_with_wiki_page_tree() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        String inputDir = "src/test/resources/wiki_page_tree";
        assertThat(outputPath).isEmptyDirectory();
        int exitCode = cmd.execute("convert", "--converter=COPYING", "--input-dir="+ inputDir, "-o=" + outputPath, "--file-extension", "wiki");
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputPath).isDirectoryContaining(path -> path.getFileName().toString().equals("confluence-content-model.json"));
        assertThat(outputPath).isDirectoryContaining("glob:**/*.wiki");
    }

    @Test
    void invoke_md2wiki_converter() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        String inputDir = "src/test/resources/markdown_example";
        assertThat(outputPath).isEmptyDirectory();
        int exitCode = cmd.execute("convert", "--converter=MD2WIKI", "--input-dir="+ inputDir, "-o=" + outputPath);
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputPath).isDirectoryContaining(path -> path.getFileName().toString().equals("confluence-content-model.json"));
        assertThat(outputPath.resolve("index.wiki")).isRegularFile().content().doesNotContain("Header");
    }

    @Test
    void invoke_md2wiki_converter_no_remove_first_header() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        String inputDir = "src/test/resources/markdown_example";
        assertThat(outputPath).isEmptyDirectory();
        int exitCode = cmd.execute("convert", "--converter=MD2WIKI", "--input-dir="+ inputDir, "-o=" + outputPath, "--title-remove-from-content=false");
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputPath).isDirectoryContaining(path -> path.getFileName().toString().equals("confluence-content-model.json"));
        assertThat(outputPath.resolve("index.wiki")).isRegularFile().content().contains("Header");
    }


    @Test
    void invoke_view2md_converter() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        String inputDir = "src/test/resources/view_single_page";
        assertThat(outputPath).isEmptyDirectory();
        int exitCode = cmd.execute("convert", "--converter=VIEW2MD", "--input-dir="+ inputDir, "-o=" + outputPath);
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputPath.resolve("65551.md")).isRegularFile().content().contains("Share your page with a team member");
        assertThat(outputPath.resolve("65551_attachments/welcome.png")).isRegularFile().content();
    }
}