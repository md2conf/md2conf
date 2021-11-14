package io.github.md2conf.converter;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class TitleExtractor {


    public static String extractTitle(Path path, ExtractTitleStrategy strategy){
        switch (strategy) {
            case FROM_FILENAME:
                return FilenameUtils.getBaseName(path.toString());
            case FROM_FIRST_HEADER:
                return readHeaderFromFile(path);
            default:
                throw new IllegalArgumentException("such title extractor is not implemented");
        }
    }

    private static String readHeaderFromFile(Path path)  {
        String res = null;
        String extension = FilenameUtils.getExtension(path.toString());
        if (extension.equalsIgnoreCase("wiki")) {
            res = readFirstWikiHeader(path);
        } else {
            throw new IllegalArgumentException("Extracting title from non-wiki extension is not implemented");
            //todo read first xml header
        }
        return res;

    }

    private static String readFirstWikiHeader(Path path)  {
        Optional<String> lineWithHeader;
        try {
            lineWithHeader = Files.lines(path)
                                  .filter(v -> v.trim().startsWith("h1.") || v.trim().startsWith("h2.") || v.trim().startsWith("h3."))
                                  .findFirst();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return lineWithHeader.map(s -> s.trim()
                                        .replaceFirst("h[123]\\.", "").trim())
                             .orElseThrow(() -> new IllegalArgumentException("Cannot extract title from wiki content at path " + path));

    }
}
