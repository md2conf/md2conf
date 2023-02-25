package io.github.md2conf.title.processor;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.md2conf.title.processor.WikiTitleUtil.isConfluenceWikiHeaderLine;

public class WikiTitleRemover {

    private static final Logger logger = LoggerFactory.getLogger(WikiTitleRemover.class);

    /**
     * remove first title from content at path
     * @param path content path
     */
    public static void removeTitle(Path path) {
        AtomicBoolean firstTitleFound = new AtomicBoolean(false);
        try (Stream<String> lines = Files.lines(path)) {
            String res = lines
                    .filter(v -> {
                        if (!firstTitleFound.get()) {
                            if (isConfluenceWikiHeaderLine(v)){
                                firstTitleFound.set(true);
                                return false;
                            };
                        }
                        return true;
                    })
                    .collect(Collectors.joining(System.lineSeparator()));
            FileUtils.write(path.toFile(), res, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Cannot remove title from content at path {}", path);
            throw new RuntimeException(e);
        }
    }
}
