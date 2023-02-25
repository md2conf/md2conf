package io.github.md2conf.title.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class WikiTitleExtractor implements TitleExtractor {
    @Override
    public String extractTitle(Path path) throws IOException {
        Optional<String> lineWithHeader;
        try (Stream<String> lines = Files.lines(path)) {
            lineWithHeader = lines
                    .filter(WikiTitleUtil::isConfluenceWikiHeaderLine)
                    .findFirst();
        }
        return lineWithHeader.map(s -> s.trim()
                        .replaceFirst("h[123]\\.", "").trim())
                .orElseThrow(() -> new IllegalArgumentException("Cannot extract title from wiki content at path " + path));
    }

}
