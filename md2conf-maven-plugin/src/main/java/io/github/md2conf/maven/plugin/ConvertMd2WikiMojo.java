package io.github.md2conf.maven.plugin;

import io.github.md2conf.command.subcommand.Md2WikiConvertCommand;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "convert-md2wiki")
public class ConvertMd2WikiMojo extends AbstractMd2ConfMojo{

    @Override
    public void execute() {
        if (this.skip) {
            getLog().info("md2conf plugin convert-md2wiki skipped ('skip' is enabled)");
            return;
        }
        Md2WikiConvertCommand.convertMd2Wiki(getMd2WikiConvertOptions(), getIndexerOptions(), getTitleProcessingOptions());
    }
}
