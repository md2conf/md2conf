package io.github.md2conf.title.processor;

import java.io.IOException;
import java.nio.file.Path;

public interface TitleExtractor {

    String extractTitle(Path path) throws IOException;
}
