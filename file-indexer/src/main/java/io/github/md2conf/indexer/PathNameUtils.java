package io.github.md2conf.indexer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathNameUtils {

    public static String ATTACHMENTS_SUFFIX = "_attachments"; //todo change to ".attach"

    public static Path attachmentsDirectoryByPagePath(Path path) {
        return Path.of(removeExtension(path) + ATTACHMENTS_SUFFIX);
    }

    public static Path removeExtension(Path path) {
        return Paths.get(path.toString().substring(0, path.toString().lastIndexOf('.')));
    }
}