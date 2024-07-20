package io.github.md2conf.model.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.github.md2conf.model.ConfluenceContentModel;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class ModelFilesystemUtil {

    public static String DEFAULT_FILE_NAME = "confluence-content-model.json";
    private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper(new JsonFactory());

    static {
        JSON_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected static ConfluenceContentModel readFromYamlOrJson(File file) {
        log.info("Reading model from {}", file.getAbsolutePath());
        try {
            return JSON_OBJECT_MAPPER.readValue(file, ConfluenceContentModel.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read model from file or directory with file "confluence-content-model.json"
     *
     * @param inputPath file or directory with file "confluence-content-model.json"
     * @return - ConfluenceContentModel
     */
    public static ConfluenceContentModel readModel(Path inputPath) {
        Path modelFilePath = inputPath.toFile().isDirectory() ? inputPath.resolve(ModelFilesystemUtil.DEFAULT_FILE_NAME) : inputPath;
        if (!modelFilePath.toFile().exists()) {
            throw new IllegalArgumentException("File doesn't exists at path " + modelFilePath);
        }
        return readFromYamlOrJson(modelFilePath.toFile());
    }


    /**
     * @param confluenceContentModel - confluenceContentModel to save
     * @param outputPath - path to save confluenceContentModel
     * @return Path to saved file
     */
    public static File saveConfluenceContentModelAtPath(ConfluenceContentModel confluenceContentModel, Path outputPath) {
        if (outputPath.toFile().exists() && !outputPath.toFile().isDirectory()) {
            throw new IllegalArgumentException("Output path is not a directory");
        }
        if (!outputPath.toFile().exists()) {
            createDirectories(outputPath);
        }
        File jsonFile = new File(outputPath.toFile(), DEFAULT_FILE_NAME);
        ObjectWriter writer = JSON_OBJECT_MAPPER.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(jsonFile, confluenceContentModel);
        } catch (IOException e) {
            throw new RuntimeException("Cannot save json to file " + jsonFile.getAbsoluteFile().getName(), e);
        }
        return jsonFile;
    }

    private static void createDirectories(Path directoryPath) {
        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create directory '" + directoryPath.toAbsolutePath() + "'", e);
        }
    }
}
