package io.github.md2conf.toolset;

import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.toolset.subcommand.Md2WikiConvertCommand;
import io.github.md2conf.toolset.subcommand.View2MdConvertCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.file.Path;

import static io.github.md2conf.model.util.ModelReadWriteUtil.readFromYamlOrJson;

@CommandLine.Command(name = "convert",
        subcommands = {Md2WikiConvertCommand.class, View2MdConvertCommand.class},
        description = "Convert")
public class ConvertCommand {

    private final static Logger logger = LoggerFactory.getLogger(ConvertCommand.class);

    public static class ConvertOptions {

        @CommandLine.Option(names = { "--model-path"}, required = false, description = "Model path directory") //todo rework
        public Path modelPath;
        @CommandLine.Option(names = {"-o", "--output-dir"}, required = true, description = "Output directory")
        public Path outputDirectory;
    }

    public static ConfluenceContentModel loadConfluenceContentModel(Path confluenceContentModelPath) {
        Path modelFilePath;
        if (confluenceContentModelPath.toFile().isDirectory()){
            modelFilePath = findFilePathWithModel(confluenceContentModelPath);
        }else{
            modelFilePath = confluenceContentModelPath;
        }
        if (!modelFilePath.toFile().exists()) {
            throw new IllegalArgumentException("File doesn't exists at path " + modelFilePath);
        }
        return readFromYamlOrJson(modelFilePath.toFile());
    }

    public static Path findFilePathWithModel(Path searchDir) {
        var result = searchDir.resolve(ConfluenceContentModel.DEFAULT_FILE_NAME);
        logger.info("Load confluence content model from {}", result);
        return result;
    }

}
