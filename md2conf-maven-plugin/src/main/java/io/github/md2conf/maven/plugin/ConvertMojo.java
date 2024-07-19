package io.github.md2conf.maven.plugin;

import io.github.md2conf.toolset.ConvertOldCommand;
import io.github.md2conf.toolset.IndexCommand;
import io.github.md2conf.toolset.subcommand.Md2WikiConvertCommand;
import io.github.md2conf.toolset.subcommand.View2MdConvertCommand;
import org.apache.maven.plugins.annotations.Mojo;

import java.nio.file.Path;

@Mojo(name = "convert")
public class ConvertMojo extends AbstractMd2ConfMojo{


    @Override
    public void execute() {
        if (this.skip) {
            getLog().info("md2conf plugin convert skipped ('skip' is enabled)");
            return;
        }
        Md2WikiConvertCommand.Md2WikiConvertOptions md2WikiConvertOptions = getConvertOptions();
        IndexCommand.IndexerOptions indexerOptions = getIndexerOptions();
        Path modelPath = getConfluenceContentModelPath()==null? null: getConfluenceContentModelPath().toPath();
        View2MdConvertCommand.FormatOptions formatOptions = getFormatOptions();
        ConvertOldCommand.convert(md2WikiConvertOptions, indexerOptions, modelPath, formatOptions);
    }
}
