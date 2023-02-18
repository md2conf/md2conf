package io.github.md2conf.title.processor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TitleUtil {
    private static final Logger logger = LoggerFactory.getLogger(TitleUtil.class);


    public static void removeTitleFromContentAtPath(String title, Path path) {
        try (Stream<String> lines = Files.lines(path)) {
            String res = lines.filter(v -> !(isConfluenceWikiHeaderLine(v) && v.contains(title)))
                    .collect(Collectors.joining(System.lineSeparator()));
            FileUtils.write(path.toFile(), res, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Cannot remove title {} from path {}", title, path);
            throw new RuntimeException(e);
        }
    }



    protected static boolean isConfluenceWikiHeaderLine(String v) {
        String s = v.trim();
        return (s.startsWith("h1.")
                || s.startsWith("h2.")
                || s.startsWith("h3."))
                && s.length()>3;
    }
}

