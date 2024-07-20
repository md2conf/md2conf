package io.github.md2conf.command;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.md2conf.indexer.PagesStructurePrinter;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.StringWriter;

import static io.github.md2conf.command.TestUtil.addAppender;
import static io.github.md2conf.command.TestUtil.getCommandLine;
import static org.assertj.core.api.Assertions.assertThat;

class IndexCommandTest {

    @Test
    void when_invokeNoParams_then_missingRequiredArgumentPrinted() {
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        CommandLine cmd = getCommandLine(swOut, swErr);
        int exitCode = cmd.execute("index");
        assertThat(exitCode).isNotZero();
        assertThat(swOut.toString()).isEmpty();
        assertThat(swErr.toString()).isNotEmpty().doesNotContain("Exception")
                .contains("Usage: md2conf index (-i=<inputDirectory>")
                .contains("Missing required argument");
    }

    @Test
    void when_invokeWithInputDirectory_then_pageStructurePrinted() {

        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        addAppender(listAppender, PagesStructurePrinter.class);
        addAppender(listAppender, IndexCommand.class);

        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        CommandLine cmd = getCommandLine(swOut, swErr);
        String inputDir = "src/test/resources/markdown_example";
        int exitCode = cmd.execute("index", "-i", inputDir );
        assertThat(exitCode).isZero();
        assertThat(swErr.toString()).isEmpty();
        assertThat(listAppender.list).hasSize(4);
        assertThat(listAppender.list.get(0).getFormattedMessage())
                .isEqualTo("Indexing path src/test/resources/markdown_example");
        assertThat(listAppender.list.get(2).getFormattedMessage())
                .isEqualTo("Page structure is:");

    }



}