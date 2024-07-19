package io.github.md2conf.toolset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.file.Path;

import static io.github.md2conf.toolset.DumpCommand.dump;

@CommandLine.Command(name = "dumpcon", aliases = "dump-and-convert", description = "Dump content from Confluence instance, convert using VIEW2MD converter to directory tree with markdown files and binary attachments")
public class DumpconCommand implements Runnable {
    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    private final static Logger logger = LoggerFactory.getLogger(DumpconCommand.class);

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    PublishCommand.ConfluenceOptions confluenceOptions;

    @CommandLine.ArgGroup(exclusive = false, heading = "Format options for view2md converter:\n", order = 4)
    ConvertCommand.FormatOptions formatOptions;

    @CommandLine.Option(names = {"-o", "--output-dir"}, description = "output directory")
    protected Path outputDirectory;

    @Override
    public void run() {
        dumpcon(confluenceOptions, formatOptions, outputDirectory);
    }

    public static void dumpcon(PublishCommand.ConfluenceOptions confluenceOptions, ConvertCommand.FormatOptions formatOptions, Path outputDirectory){
        var intermediateDir = outputDirectory.resolve(".dump");
        dump(confluenceOptions, intermediateDir);
        ConvertCommand.ConvertOptions convertOptions = new ConvertCommand.ConvertOptions();
        convertOptions.outputDirectory = outputDirectory;
        convertOptions.converter = ConvertCommand.ConverterType.VIEW2MD;
        ConvertCommand.convert(convertOptions, new IndexCommand.IndexerOptions(), intermediateDir, formatOptions);
    }

}
