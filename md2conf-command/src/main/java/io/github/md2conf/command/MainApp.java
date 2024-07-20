package io.github.md2conf.command; //todo rename to command?

import picocli.CommandLine;

@CommandLine.Command(
        name = "md2conf",
        subcommands = {
                ConpubCommand.class,
                ConvertCommand.class,
                DumpCommand.class,
                DumpconCommand.class,
                IndexCommand.class,
                PublishCommand.class,
                CommandLine.HelpCommand.class},
        description = "Set of tools to deal with markdown files and Confluence: publish, dump, convert"
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
