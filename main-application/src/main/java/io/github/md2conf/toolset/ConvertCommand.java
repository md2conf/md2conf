package io.github.md2conf.toolset;

import io.github.md2conf.toolset.subcommand.Md2WikiConvertCommand;
import io.github.md2conf.toolset.subcommand.View2MdConvertCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.file.Path;

@CommandLine.Command(name = "convert",
        subcommands = {Md2WikiConvertCommand.class, View2MdConvertCommand.class},
        description = "Convert")
public class ConvertCommand {

    private final static Logger logger = LoggerFactory.getLogger(ConvertCommand.class);

    public static class ConvertOptions {

        @CommandLine.Option(names = {"-o", "--output-dir"}, required = true, description = "Output directory")
        public Path outputDirectory; // move to output options and use with dump command too
    }

}
