package io.github.md2conf.toolset;

import io.github.md2conf.toolset.subcommand.Md2WikiConvertCommand;
import io.github.md2conf.toolset.subcommand.View2MdConvertCommand;
import picocli.CommandLine;

import java.nio.file.Path;

import static io.github.md2conf.toolset.DumpCommand.dump;

@CommandLine.Command(name = "dumpcon", aliases = "dump-and-convert", description = "Dump content from Confluence instance, convert using VIEW2MD converter to directory tree with markdown files and binary attachments")
public class DumpconCommand implements Runnable {
    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    PublishCommand.ConfluenceOptions confluenceOptions;

    @CommandLine.ArgGroup(exclusive = false, heading = "Format options for view2md converter:\n", order = 4)
    View2MdConvertCommand.View2MdConvertOptions view2MdConvertOptions;

    @Override
    public void run() {
        dumpcon(confluenceOptions, view2MdConvertOptions, view2MdConvertOptions.outputDirectory);
    }

    public static void dumpcon(PublishCommand.ConfluenceOptions confluenceOptions, View2MdConvertCommand.View2MdConvertOptions view2MdConvertOptions, Path outputDirectory){
        var intermediateDir = outputDirectory.resolve(".dump");
        dump(confluenceOptions, intermediateDir);
        Md2WikiConvertCommand.Md2WikiConvertOptions md2WikiConvertOptions = new Md2WikiConvertCommand.Md2WikiConvertOptions(); //todo
        md2WikiConvertOptions.outputDirectory = outputDirectory;
        View2MdConvertCommand.convertView2Md(view2MdConvertOptions);
    }

}
