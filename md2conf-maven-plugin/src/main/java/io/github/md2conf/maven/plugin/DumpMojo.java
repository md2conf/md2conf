package io.github.md2conf.maven.plugin;

import io.github.md2conf.command.DumpCommand;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "dump")
public class DumpMojo extends AbstractMd2ConfMojo{
    @Override
    public void execute() {
        if (this.skip) {
            getLog().info("md2conf plugin dump skipped ('skip' is enabled)");
            return;
        }
        DumpCommand.dump(getConfluenceOptions(),  getOutputDirectoryAsPath());
    }

}
