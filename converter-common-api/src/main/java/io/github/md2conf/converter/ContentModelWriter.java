package io.github.md2conf.converter;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.github.md2conf.model.ConfluenceContent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ContentModelWriter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static void saveConfluenceContentModelToFilesystem(ConfluenceContent confluenceContent, Path outputPath){
        if (outputPath.toFile().exists()&& !outputPath.toFile().isDirectory()){
            throw new IllegalArgumentException("Output path is not a directory");
        }
        if (!outputPath.toFile().exists()){
            createDirectories(outputPath);
        }
        File jsonFile = new File(outputPath.toFile(), ConfluenceContent.DEFAULT_FILE_NAME);
        ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(jsonFile, confluenceContent);
        } catch (IOException e) {
            throw new RuntimeException("Cannot save json to file "+ jsonFile.getAbsoluteFile().getName(), e);
        }
    }

    private static void createDirectories(Path directoryPath) {
        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create directory '" + directoryPath.toAbsolutePath() + "'", e);
        }
    }
}
