package io.github.md2conf.flexmart.ext.crosspage.links;

import com.vladsch.flexmark.ast.InlineLinkNode;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import java.nio.file.Path;

public class CrosspageLink extends InlineLinkNode {

    private Path path;

    public CrosspageLink(Link other) {
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

    @Override
    public void setTextChars(BasedSequence textChars) {

    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
