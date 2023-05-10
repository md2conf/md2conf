package io.github.md2conf.maven.plugin;

import io.github.md2conf.toolset.ConpubCommand;
import io.github.md2conf.toolset.ConvertCommand;
import io.github.md2conf.toolset.PublishCommand;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "conpub")
public class ConPubMojo extends AbstractMd2ConfMojo{
    @Override
    public void execute() {
        if (this.skip) {
            getLog().info("md2conf plugin conpub skipped ('skip' is enabled)");
            return;
        }
        ConvertCommand.ConvertOptions convertOptions = getConvertOptions();
        PublishCommand.PublishOptions publishOptions = getPublishOptions();
        ConpubCommand.conpub(convertOptions, publishOptions);
    }

}
