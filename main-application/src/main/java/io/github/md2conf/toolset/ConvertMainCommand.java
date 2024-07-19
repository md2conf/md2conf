package io.github.md2conf.toolset;

import io.github.md2conf.toolset.subcommand.Md2WikiConvertCommand;
import io.github.md2conf.toolset.subcommand.View2MdConvertCommand;
import picocli.CommandLine;

@CommandLine.Command(name = "convert",
        subcommands = {Md2WikiConvertCommand.class, View2MdConvertCommand.class},
        description = "Convert")
public class ConvertMainCommand {
}
