package io.github.md2conf.toolset;

import io.github.md2conf.toolset.subcommand.View2MdConvertCommand;
import picocli.CommandLine;

import static io.github.md2conf.toolset.DumpCommand.dump;

@CommandLine.Command(name = "dumpcon", aliases = "dump-and-convert", description = "Dump content from Confluence instance, convert using VIEW2MD converter to directory tree with markdown files and binary attachments")
public class DumpconCommand implements Runnable {
    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    PublishCommand.ConfluenceOptions confluenceOptions;

    @CommandLine.ArgGroup(exclusive = false, heading = "Format options for view2md converter:\n", order = 4)
    View2MdConvertCommand.View2MdConvertOptions view2MdConvertOptions;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1", heading = "Markdown format options:\n")
    View2MdConvertCommand.MarkdownFormatOptions markdownFormatOptions;

    @Override
    public void run() {
        dumpcon(confluenceOptions, view2MdConvertOptions, markdownFormatOptions);
    }

    public static void dumpcon(PublishCommand.ConfluenceOptions confluenceOptions,
                               View2MdConvertCommand.View2MdConvertOptions view2MdConvertOptions,
                               View2MdConvertCommand.MarkdownFormatOptions markdownFormatOptions){
        var intermediateDir = view2MdConvertOptions.outputDirectory.resolve(".dump");
        dump(confluenceOptions, intermediateDir);
        View2MdConvertCommand.convertView2Md(view2MdConvertOptions, markdownFormatOptions);
    }

}
