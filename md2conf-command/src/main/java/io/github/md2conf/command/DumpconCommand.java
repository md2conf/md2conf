package io.github.md2conf.command;

import io.github.md2conf.command.subcommand.View2MdConvertCommand;
import picocli.CommandLine;

import java.nio.file.Path;

import static io.github.md2conf.command.DumpCommand.dump;

@CommandLine.Command(name = "dumpcon", aliases = "dump-and-convert", description = "Dump content from Confluence instance, convert using VIEW2MD converter to directory tree with markdown files and binary attachments")
public class DumpconCommand implements Runnable {
    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    PublishCommand.ConfluenceOptions confluenceOptions;

    @CommandLine.Option(names = {"-o", "--output-dir"}, description = "output directory")
    protected Path outputDirectory;

    @CommandLine.ArgGroup(exclusive = false, heading = "Markdown format options:\n")
    View2MdConvertCommand.MarkdownFormatOptions markdownFormatOptions;

    @Override
    public void run() {
        View2MdConvertCommand.MarkdownFormatOptions markdownFormatOptionsLocal = markdownFormatOptions == null ? new View2MdConvertCommand.MarkdownFormatOptions() : markdownFormatOptions;
        dumpcon(confluenceOptions, outputDirectory, markdownFormatOptionsLocal);
    }

    public static void dumpcon(PublishCommand.ConfluenceOptions confluenceOptions,
                               Path outputDirectory,
                               View2MdConvertCommand.MarkdownFormatOptions markdownFormatOptions) {
        var intermediateDir = outputDirectory.resolve(".dump");
        dump(confluenceOptions, intermediateDir);
        View2MdConvertCommand.View2MdConvertOptions convertOptions = new View2MdConvertCommand.View2MdConvertOptions();
        convertOptions.modelPath = intermediateDir;
        convertOptions.outputDirectory = outputDirectory;
        View2MdConvertCommand.convertView2Md(convertOptions, markdownFormatOptions);
    }

}
