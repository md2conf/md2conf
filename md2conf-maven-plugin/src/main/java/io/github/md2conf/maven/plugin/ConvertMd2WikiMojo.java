package io.github.md2conf.maven.plugin;

import io.github.md2conf.toolset.IndexCommand;
import io.github.md2conf.toolset.subcommand.Md2WikiConvertCommand;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "convert-md2wiki")
public class ConvertMd2WikiMojo extends AbstractMd2ConfMojo{


    @Override
    public void execute() {
        if (this.skip) {
            getLog().info("md2conf plugin convert-md2wiki skipped ('skip' is enabled)");
            return;
        }
        Md2WikiConvertCommand.Md2WikiConvertOptions md2WikiConvertOptions = getMd2WikiConvertOptions();
        IndexCommand.IndexerOptions indexerOptions = getIndexerOptions();
        Md2WikiConvertCommand.convertMd2Wiki(md2WikiConvertOptions, indexerOptions, );
    }
}
