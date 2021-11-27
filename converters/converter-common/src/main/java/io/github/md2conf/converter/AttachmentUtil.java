package io.github.md2conf.converter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.md2conf.indexer.PathNameUtils.attachmentsDirectoryByPagePath;

public class AttachmentUtil {

    public static Map<String, String> toAttachmentsMap(List<Path> pathList) {
        return pathList.stream()
                       .collect(Collectors.toMap(
                               path -> FilenameUtils.getBaseName(path.toString()),
                               Path::toString));
    }

    public static List<Path> copyPageAttachments(List<Path> sourceAttachments, Path destinationPagePath) throws IOException {
        List<Path> copiedAttachments = new ArrayList<>();
        for (Path sourceAttachment : sourceAttachments){
            Path directoryWithAttachments = attachmentsDirectoryByPagePath(destinationPagePath);
            if(!directoryWithAttachments.toFile().mkdirs()){
                throw new IOException("Cannot create dirs for path " + directoryWithAttachments);
            }
            copiedAttachments.add(PathUtils.copyFileToDirectory(sourceAttachment, directoryWithAttachments));
        }
        return copiedAttachments;
    }
}
