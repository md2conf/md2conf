package io.github.md2conf.maven.plugin;

import io.github.md2conf.toolset.DumpconCommand;
import org.apache.maven.plugins.annotations.Mojo;

import java.nio.file.Path;

@Mojo(name = "dumpcon")
public class DumpConMojo extends AbstractMd2ConfMojo{
    @Override
    public void execute() {
        if (this.skip) {
            getLog().info("md2conf plugin dumpcon skipped ('skip' is enabled)");
            return;
        }
        DumpconCommand.dumpcon(getConfluenceOptions(),  getOutputDirectoryAsPath());
    }

    public Path getOutputDirectoryAsPath() {
        if (outputDirectory != null) {
            return outputDirectory.toPath();
        } else {
            return null;
        }
    }

}
