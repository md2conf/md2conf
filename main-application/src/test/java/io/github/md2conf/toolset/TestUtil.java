package io.github.md2conf.toolset;

import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TestUtil {

    public static CommandLine getCommandLine(StringWriter swOut, StringWriter swErr) {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        return cmd;
    }
}
