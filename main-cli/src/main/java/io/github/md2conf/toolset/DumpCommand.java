package io.github.md2conf.toolset;

import picocli.CommandLine;

@CommandLine.Command(name = "dump",description = "Dump content from Confluence instance")
public class DumpCommand {
    @CommandLine.Mixin
    LoggingMixin loggingMixin;
}
