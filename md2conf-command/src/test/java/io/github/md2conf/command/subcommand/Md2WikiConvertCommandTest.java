package io.github.md2conf.command.subcommand;

import io.github.md2conf.command.MainApp;
import io.github.md2conf.title.processor.TitleExtractStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

import static io.github.md2conf.command.TestUtil.getCommandLine;
import static org.assertj.core.api.Assertions.assertThat;

class Md2WikiConvertCommandTest {
    @TempDir
    private Path emptyDir;

    @TempDir
    private Path outputPath;

    @Test
    void when_invokeNoParams_then_missingRequiredArgumentPrinted() {
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        CommandLine cmd = getCommandLine(swOut, swErr);
        int exitCode = cmd.execute("convert", "md2wiki");
        assertThat(exitCode).isNotZero();
        assertThat(swOut.toString()).isEmpty();
        assertThat(swErr.toString()).isNotEmpty().doesNotContain("Exception")
                .contains("Usage: md2conf")
                .contains("Missing required argument");
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
    void when_invokeMd2wikiConverterWithTitleExtractFromFileName_then_DoNotRemovFirstHeaderFromContent() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        String inputDir = "src/test/resources/markdown_example";
        assertThat(outputPath).isEmptyDirectory();
        int exitCode = cmd.execute("convert", "md2wiki", "--input-dir="+ inputDir, "-o=" + outputPath, "--title-extract="+ TitleExtractStrategy.FROM_FILENAME);
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputPath).isDirectoryContaining(path -> path.getFileName().toString().equals("confluence-content-model.json"));
        assertThat(outputPath.resolve("index.wiki")).isRegularFile().content().contains("Header");
    }

    @Test
    void when_invokeMd2wikiConverterWithTitleExtractFromFirstHeader_then_RemoveFirstHeaderFromContent() {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        String inputDir = "src/test/resources/markdown_example";
        assertThat(outputPath).isEmptyDirectory();
        int exitCode = cmd.execute("convert", "md2wiki", "--input-dir="+ inputDir, "-o=" + outputPath, "--title-extract="+ TitleExtractStrategy.FROM_FIRST_HEADER);
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputPath).isDirectoryContaining(path -> path.getFileName().toString().equals("confluence-content-model.json"));
        assertThat(outputPath.resolve("index.wiki")).isRegularFile().content().doesNotContain("Header");
    }


}