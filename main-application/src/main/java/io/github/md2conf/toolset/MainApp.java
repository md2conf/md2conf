package io.github.md2conf.toolset;

import picocli.CommandLine;

@CommandLine.Command(
        name = "md2conf",
        subcommands = {ConvertCommand.class,
                PublishCommand.class,
                ConpubCommand.class,
                DumpCommand.class,
                DumpconCommand.class,
                ModelHelpCommand.class,
                CommandLine.HelpCommand.class},
        description = "Set of tools to work with 'confluence-content-model': publish, dump, convert.",
        footer = "'confluence-content-model' is a representation of Confluence page trees and attachments on a local filesystem. See 'md2conf help model' for details."
)
public class MainApp {

    @CommandLine.Mixin
    LoggingMixin loggingMixin;


    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new MainApp());
        commandLine.setCaseInsensitiveEnumValuesAllowed(true);
        commandLine.setExecutionStrategy(LoggingMixin::executionStrategy).execute(args);
    }
}
