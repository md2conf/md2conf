package io.github.md2conf.toolset;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.StringWriter;

import static io.github.md2conf.toolset.TestUtil.getCommandLine;
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



}