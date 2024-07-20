package io.github.md2conf.maven.plugin;

import io.github.md2conf.command.subcommand.View2MdConvertCommand;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "convert-view2md")
public class ConvertView2MdMojo extends AbstractMd2ConfMojo{ //todo add tests

    @Override
    public void execute() {
        if (this.skip) {
            getLog().info("md2conf plugin convert-view2md skipped ('skip' is enabled)");
            return;
        }
        View2MdConvertCommand.View2MdConvertOptions view2MdConvertOptions = getFormatOptions();
        View2MdConvertCommand.MarkdownFormatOptions markdownFormatOptions = getMarkdownFormatOptions();
        View2MdConvertCommand.convertView2Md(view2MdConvertOptions, markdownFormatOptions);
    }
}
