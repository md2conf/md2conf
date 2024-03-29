package io.github.md2conf.model.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.github.md2conf.model.ConfluenceContentModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModelReadWriteUtil {

    static ObjectMapper jsonObjectMapper = new ObjectMapper(new JsonFactory());

    static {
        jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ConfluenceContentModel readFromYamlOrJson(File file) {
        try {
            return jsonObjectMapper.readValue(file, ConfluenceContentModel.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        File jsonFile = new File(outputPath.toFile(), ConfluenceContentModel.DEFAULT_FILE_NAME);
        ObjectWriter writer = jsonObjectMapper.writer(new DefaultPrettyPrinter());
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
