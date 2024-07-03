package io.github.md2conf.markdown.formatter;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.Escaping;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImageAttachmentUrlReplacer {

    NodeVisitor visitor = new NodeVisitor(
            new VisitHandler<>(Image.class, this::visit)
    );

    private final Map<String, String> fileNameByParentDir;

    public ImageAttachmentUrlReplacer(List<Path> attachments) {
        this.fileNameByParentDir = attachments.stream()
                .distinct()
                .collect(Collectors.toMap(v -> v.getFileName().toString(), getParentDirName()));
    }

    private static Function<Path, String> getParentDirName() {
        return v -> {
            if (v.getParent() != null && v.getParent().getParent() != null) {
                return v.getParent().getParent().relativize(v).toString();
            } else {
                return "";
            }
        };
    }

    public void replaceUrl(Node node) {
        visitor.visit(node);
    }

    private void visit(Image node) {
        if (node.getPageRef().startsWith("/download/attachments")) {
            String fileName = extractFileNameFromConfluenceLink(node.getPageRef().toString());
            String decodedFilename = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            if (fileNameByParentDir.containsKey(decodedFilename)) {
                BasedSequence url = BasedSequence.of(Escaping.percentEncodeUrl(fileNameByParentDir.get(decodedFilename)));
                node.setUrl(url);
                node.setUrlChars(url);
                node.setText(BasedSequence.of(decodedFilename));
            }
        }
    }

    public static String extractFileNameFromConfluenceLink(String url) {
        String[] parts = url.split("/");
        if (parts.length >= 5) {
            String pathWithHttpOptions = parts[4];
            if (pathWithHttpOptions.contains("?")) {
                return pathWithHttpOptions.substring(0, pathWithHttpOptions.lastIndexOf("?"));
            } else {
                return pathWithHttpOptions;
            }
        }
        return url;
    }
}
