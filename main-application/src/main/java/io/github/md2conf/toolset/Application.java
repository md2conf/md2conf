package io.github.md2conf.toolset;

import picocli.CommandLine;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command
public class Application implements Callable<Void> {

    @CommandLine.Option(names = { "-i", "--input-dir" }, required = true, description = "input directory")
    private Path inputDirectory;

    @CommandLine.Option(names = { "-wd", "--work-dir" }, description = "working directory")
    private Path workingDirectory;


    public static void main(String[] args) {
        new CommandLine(new Application()).execute(args);
    }

    @Override
    public Void call() throws Exception {

        initParameters();

        return null;
    }

    private void initParameters() {
        if (workingDirectory==null){
            workingDirectory = new File(inputDirectory.toFile(), ".md2conf/out").toPath();
        }
    }
}
