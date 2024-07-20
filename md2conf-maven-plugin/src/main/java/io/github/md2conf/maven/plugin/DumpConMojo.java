package io.github.md2conf.maven.plugin;

import io.github.md2conf.command.DumpconCommand;
import io.github.md2conf.command.subcommand.View2MdConvertCommand;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "dumpcon")
public class DumpConMojo extends AbstractMd2ConfMojo{
    @Override
    public void execute() {
        if (this.skip) {
            getLog().info("md2conf plugin dumpcon skipped ('skip' is enabled)");
            return;
        }
        View2MdConvertCommand.MarkdownFormatOptions markdownFormatOptions = getMarkdownFormatOptions();
        DumpconCommand.dumpcon(getConfluenceOptions(), getOutputDirectoryAsPath(), markdownFormatOptions);
    }

}
