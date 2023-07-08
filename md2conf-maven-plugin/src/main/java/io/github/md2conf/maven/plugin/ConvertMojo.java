package io.github.md2conf.maven.plugin;

import io.github.md2conf.toolset.ConvertCommand;
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
        ConvertCommand.ConvertOptions convertOptions = getConvertOptions();
        ConvertCommand.IndexerOptions indexerOptions = getIndexerOptions();
        Path modelPath = getConfluenceContentModelPath()==null? null: getConfluenceContentModelPath().toPath();
        ConvertCommand.convert(convertOptions, indexerOptions, modelPath);
    }
}
