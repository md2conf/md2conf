package io.github.md2conf.title.processor;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Path;

public class FirstHeaderTitleExtractor implements TitleExtractor {

    private final TitleExtractor wikiTitleExtractor = new WikiTitleExtractor();
    private final TitleExtractor markdownTitleExtractor = new MarkdownTitleExtractor();

    @Override
    public String extractTitle(Path path) throws IOException {
        String extension = FilenameUtils.getExtension(path.toString());
        if (extension.equalsIgnoreCase("wiki")){
            return wikiTitleExtractor.extractTitle(path);
        }
        else if (extension.equalsIgnoreCase("md")){
            return markdownTitleExtractor.extractTitle(path);
        }
        else {
            throw new IllegalArgumentException("FirstHeaderTitleExtractor for file extension \""
                    + extension +
                    "\" is not implemented");
        }
    }
}
