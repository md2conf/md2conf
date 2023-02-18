package io.github.md2conf.title.processor;

import java.io.IOException;
import java.nio.file.Path;

public class DefaultTitleExtractor implements TitleExtractor {
    private final TitleExtractStrategy titleExtractStrategy;
    private final TitleExtractor filenameTitleExtractor = new FilenameTitleExtractor();
    private final TitleExtractor firstHeaderTitleExtractor = new FirstHeaderTitleExtractor();

    public DefaultTitleExtractor(TitleExtractStrategy titleExtractStrategy) {
        this.titleExtractStrategy = titleExtractStrategy;
    }

    @Override
    public String extractTitle(Path path) throws IOException {
        switch (titleExtractStrategy){
            case FROM_FILENAME:
                return filenameTitleExtractor.extractTitle(path);
            case FROM_FIRST_HEADER:
                return firstHeaderTitleExtractor.extractTitle(path);
            default:
                throw new IllegalArgumentException("titleExtractStrategy" + titleExtractStrategy + " is not implemented");
        }
    }
}
