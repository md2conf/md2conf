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
        description = "Set of tools to work with 'confluence-content-model': publish, dump, convert.",
        footer = "'confluence-content-model' is a representation of Confluence page trees and attachments on a local filesystem. See 'md2conf help md2conf' for details."
)
public class MainApp {
    public static void main(String[] args) {
        new CommandLine(new MainApp()).execute(args);
    }
}
