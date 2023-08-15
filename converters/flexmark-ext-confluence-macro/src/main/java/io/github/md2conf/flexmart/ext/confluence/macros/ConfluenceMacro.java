package io.github.md2conf.flexmart.ext.confluence.macros;

import com.vladsch.flexmark.ast.HtmlCommentBlock;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class ConfluenceMacro extends HtmlCommentBlock {

    private final boolean withEOL;

    public ConfluenceMacro(BasedSequence basedSequence, boolean block) {
        super(basedSequence);
        this.withEOL = block;
    }

    public boolean isWithEOL() {
        return withEOL;
    }

}
