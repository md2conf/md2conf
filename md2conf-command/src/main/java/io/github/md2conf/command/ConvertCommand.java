package io.github.md2conf.command;

import io.github.md2conf.command.subcommand.Md2WikiConvertCommand;
import io.github.md2conf.command.subcommand.View2MdConvertCommand;
import io.github.md2conf.title.processor.TitleExtractStrategy;
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
        //todo where to put?
    }

    public static class TitleProcessingOptions {
        @CommandLine.Option(names = {"--title-extract"}, description = "Strategy to extract title from file", //todo rename to TitleExtractFrom
                defaultValue = "FROM_FIRST_HEADER",
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        public TitleExtractStrategy titleExtract = TitleExtractStrategy.FROM_FIRST_HEADER;

        @CommandLine.Option(names = {"--title-prefix"}, description = "Title prefix common for all pages")
        public String titlePrefix;
        @CommandLine.Option(names = {"--title-suffix"}, description = "Title suffix common for all pages")
        public String titleSuffix;
        @CommandLine.Option(names = {"--title-child-prefixed"}, description = "Add title prefix of root page if page is a child")
        public boolean titleChildPrefixed;
        @CommandLine.Option(names = {"--title-remove-from-content"}, description = "Remove title from converted content, to avoid duplicate titles rendering in an Confluence")
        public Boolean titleRemoveFromContent;
    }

}
