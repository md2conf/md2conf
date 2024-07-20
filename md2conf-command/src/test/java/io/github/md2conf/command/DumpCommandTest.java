package io.github.md2conf.command;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.StringWriter;

import static io.github.md2conf.command.TestUtil.getCommandLine;
import static org.assertj.core.api.Assertions.assertThat;

class DumpCommandTest {

    @Test
    void when_invokeNoParams_then_missingRequiredArgumentPrinted() {
        StringWriter swOut = new StringWriter();
        StringWriter swErr = new StringWriter();
        CommandLine cmd = getCommandLine(swOut, swErr);
        int exitCode = cmd.execute("dump");
        assertThat(exitCode).isNotZero();
        assertThat(swOut.toString()).isEmpty();
        assertThat(swErr.toString()).isNotEmpty().doesNotContain("Exception")
                .contains("Usage: md2conf")
                .contains("Missing required argument");
    }

}