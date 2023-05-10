package io.github.md2conf.maven.plugin;

import io.github.md2conf.title.processor.TitleExtractStrategy;
import io.github.md2conf.toolset.ConvertCommand;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

import static io.github.md2conf.toolset.ConvertCommand.ConverterType.MD2WIKI;

@Mojo(name = "convert")
public class ConvertMojo extends AbstractMd2ConfMojo{


    @Override
    public void execute() {
        if (this.skip) {
            getLog().info("md2conf plugin convert skipped ('skip' is enabled)");
            return;
        }
        ConvertCommand.ConvertOptions convertOptions = getConvertOptions();
        ConvertCommand.convert(convertOptions);
    }
}
