package io.github.md2conf.toolset.subcommand;

import io.github.md2conf.toolset.MainApp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class Md2WikiConvertCommandTest {
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
        int exitCode = cmd.execute("convert", "md2wiki");
        assertThat(exitCode).isNotZero();
        String errOut = swErr.toString();
        assertThat(swOut.toString()).isEmpty();
        assertThat(errOut).isNotEmpty().contains("Missing required argument");
        assertThat(errOut).doesNotContain("publish").doesNotContain("Exception").contains("Missing required argument");
    }

    @Test
    void invoke_on_empty_dir() { //todo print warn from indexer
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        cmd.setCaseInsensitiveEnumValuesAllowed(true);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        int exitCode = cmd.execute("convert", "md2wiki", "--input-dir="+ emptyDir, "--output-dir="+emptyDir.resolve("out"));
        assertThat(swOut.toString()).isEmpty();
        assertThat(exitCode).isZero();
    }

    @Test
    void when_convertNonExistingDir_then_printError() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        int exitCode = cmd.execute("convert", "md2wiki", "--input-dir="+ outputPath + "/non_exists", "-v",  "-o=" + outputPath);
        String errOut = swErr.toString();
        assertThat(exitCode).isNotZero();
        assertThat(errOut).contains("NoSuchFileException");
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
        int exitCode = cmd.execute("convert", "md2wiki", "--input-dir="+ inputDir, "-o=" + outputPath);
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
        int exitCode = cmd.execute("convert", "md2wiki", "--input-dir="+ inputDir, "-o=" + outputPath, "--title-remove-from-content=false");
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputPath).isDirectoryContaining(path -> path.getFileName().toString().equals("confluence-content-model.json"));
        assertThat(outputPath.resolve("index.wiki")).isRegularFile().content().contains("Header");
    }


}