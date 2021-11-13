package io.github.md2conf.toolset;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "publish", description = "Publish content to a Confluence instance")
public class PublishCommand {
    @CommandLine.Mixin
    LoggingMixin loggingMixin;
}
