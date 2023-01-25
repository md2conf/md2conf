package io.github.md2conf.converter.md2wiki.attachment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImageFilePathUtil {
    public static List<Path> filterExistingPaths(Set<String> imageUrls, Path basePath) {
        //remove http and https links
        return imageUrls.stream()
                .filter(v -> !(v.startsWith("https://") || v.startsWith("http://")))
                .map(v -> new ImagePathCheckAndConvert(basePath).apply(v))
                .filter(Objects::nonNull)
                .map(Path::toAbsolutePath)
                .distinct()
                .collect(Collectors.toList());
    }

    public static class ImagePathCheckAndConvert implements Function<String,Path> {
        private static final Logger logger = LoggerFactory.getLogger(ImagePathCheckAndConvert.class);

        private final Path basePath;

        public ImagePathCheckAndConvert(Path basePath) {
            this.basePath = basePath;
        }

        @Override
        public Path apply(String s) {
            Path res = null;
            Path absolute = Path.of(s);
            Path relative = basePath.resolve(s);
            if (absolute.toFile().exists()) {
                res = absolute;
            } else if (relative.toFile().exists()) {
                res = relative;
            } else {
                logger.info("Image file at path {} or at path {} doesn't exists", absolute, relative);
            }
            return res;
        }
    }
}
