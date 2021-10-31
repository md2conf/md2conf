package io.github.md2conf.toolset;

import picocli.CommandLine;

@CommandLine.Command(
        name = "md2conf",
        subcommands = {ConvertCommand.class,
                PublishCommand.class,
                ConvertAndPublishCommand.class,
                DumpCommand.class,
                ModelOverviewCommand.class,
                CommandLine.HelpCommand.class},
        description = "Set of tools to publish/dump 'confluence-content-model' from/to filesystem and various converters",
        footer = "See 'md2conf help md2conf' for an overview of 'confluence-content-model' abstraction"
)
public class MainApp {
    public static void main(String[] args) {
        new CommandLine(new MainApp()).execute(args);
    }
}
