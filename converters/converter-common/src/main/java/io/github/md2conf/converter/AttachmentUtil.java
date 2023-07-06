package io.github.md2conf.converter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.md2conf.indexer.PathNameUtils.attachmentsDirectoryByPagePath;

public class AttachmentUtil {

    public static Map<String, String> toAttachmentsMap(List<Path> pathList) {
        return pathList.stream()
                .collect(Collectors.toMap(
                        path -> FilenameUtils.getName(path.toString()),
                        Path::toString));
    }

    @SafeVarargs
    public static List<Path> copyPageAttachments(Path destinationPagePath, List<Path>... sourceAttachments) throws IOException {
        List<Path> copiedAttachments = new ArrayList<>();
        List<Path> sources = Arrays.stream(sourceAttachments)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        for (Path sourceAttachment : sources) {
            File directoryWithAttachments = attachmentsDirectoryByPagePath(destinationPagePath).toFile();
            if (directoryWithAttachments.exists() && !directoryWithAttachments.isDirectory()) {
                throw new IOException("directoryWithAttachments is not directory " + directoryWithAttachments);
            } else if (!directoryWithAttachments.exists()) {
                if (!directoryWithAttachments.mkdirs()) {
                    throw new IOException("Cannot create dirs for path " + directoryWithAttachments);
                }
            }
            copiedAttachments.add(PathUtils.copyFileToDirectory(sourceAttachment,
                    directoryWithAttachments.toPath(), StandardCopyOption.REPLACE_EXISTING));
        }
        return copiedAttachments;
    }

    public static List<Path> copyAttachmentsMap(Path destinationPagePath, Map<String, String> pathMap) throws IOException {
        if (pathMap.isEmpty()){
            return Collections.emptyList();
        }
        List<Path> copiedAttachments = new ArrayList<>();
        File targetDir = attachmentsDirectoryByPagePath(destinationPagePath).toFile();
        if (targetDir.exists() && !targetDir.isDirectory()) {
            throw new IOException("targetDir is not directory " + targetDir);
        } else if (!targetDir.exists()) {
            if (!targetDir.mkdirs()) {
                throw new IOException("Cannot create dirs for path " + targetDir);
            }
        }
        for (String name : pathMap.keySet()) {
            Path sourcePath = Path.of(pathMap.get(name));
            copiedAttachments.add(PathUtils.copyFileToDirectory(sourcePath,
                    targetDir.toPath(), StandardCopyOption.REPLACE_EXISTING));
        }
        return copiedAttachments;
    }
}
