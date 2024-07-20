package io.github.md2conf.maven.plugin;

import io.github.md2conf.command.PublishCommand;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "publish")
public class PublishMojo extends AbstractMd2ConfMojo{
    @Override
    public void execute() {
        if (this.skip) {
            getLog().info("md2conf plugin publish skipped ('skip' is enabled)");
            return;
        }
        var path = confluenceContentModelPath==null? null: confluenceContentModelPath.toPath();
        PublishCommand.publish(getConfluenceOptions(), getPublishOptions(), path);
    }


}
