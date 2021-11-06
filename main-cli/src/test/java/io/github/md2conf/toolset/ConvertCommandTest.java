package io.github.md2conf.toolset;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.StringWriter;

class ConvertCommandTest {

    @Test
    void invoke_no_op_converter() {

        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        StringWriter swOut = new StringWriter();


        int exitCode = cmd.execute("convert", "--converter=no-op", "-input=/");

    }
}