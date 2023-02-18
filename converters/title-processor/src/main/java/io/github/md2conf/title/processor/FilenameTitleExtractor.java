package io.github.md2conf.title.processor;

import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;

public class FilenameTitleExtractor implements TitleExtractor {
    @Override
    public String extractTitle(Path path) {
        return FilenameUtils.getBaseName(path.toString());
    }
}
