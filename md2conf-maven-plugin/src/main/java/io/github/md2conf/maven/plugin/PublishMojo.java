package io.github.md2conf.maven.plugin;

import io.github.md2conf.toolset.PublishCommand;
import io.github.md2conf.toolset.PublishCommand.PublishOptions;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "publish")
public class PublishMojo extends AbstractMd2ConfMojo{
    @Override
    public void execute() {
        if (this.skip) {
            getLog().info("md2conf plugin publish skipped ('skip' is enabled)");
            return;
        }
        PublishOptions publishOptions = getPublishOptions();
        var path = confluenceContentModelPath==null? null: confluenceContentModelPath.toPath();
        PublishCommand.publish(publishOptions, path);
    }


}
