package io.github.md2conf.toolset;

import picocli.CommandLine;

@CommandLine.Command(
        name = "md2conf",
        subcommands = {ConvertCommand.class,
                PublishCommand.class,
                ConvertAndPublishCommand.class,
                DumpCommand.class,
                CommandLine.HelpCommand.class}
)
public class MainApp {
    public static void main(String[] args) {
        new CommandLine(new MainApp()).execute(args);
    }
}
