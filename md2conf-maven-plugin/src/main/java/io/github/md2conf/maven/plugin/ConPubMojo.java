package io.github.md2conf.maven.plugin;

import io.github.md2conf.toolset.ConpubCommand;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "conpub")
public class ConPubMojo extends AbstractMd2ConfMojo{
    @Override
    public void execute() {
        if (this.skip) {
            getLog().info("md2conf plugin conpub skipped ('skip' is enabled)");
            return;
        }
        ConpubCommand.conpub(getConvertOptions(), getIndexerOptions(), getConfluenceOptions(), getPublishOptions());
    }

}
