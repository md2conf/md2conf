package io.github.md2conf.flexmart.ext.local.image;

import com.vladsch.flexmark.ast.Image;

import java.nio.file.Path;

public class LocalImage extends Image {

    private Path path;
    private String fileName;

    public LocalImage(Image other) {
        super(other.baseSubSequence(other.getStartOffset(), other.getEndOffset()),
                other.baseSubSequence(other.getStartOffset(), other.getTextOpeningMarker().getEndOffset()),
                other.getText(),
                other.getTextClosingMarker(),
                other.getLinkOpeningMarker(),
                other.getUrl(),
                other.getTitleOpeningMarker(),
                other.getTitle(),
                other.getTitleClosingMarker(),
                other.getLinkClosingMarker()
        );
    }


    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
