package io.github.md2conf.flexmart.ext.confluence.macros;

import com.vladsch.flexmark.ast.HtmlCommentBlock;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class ConfluenceMacro extends HtmlCommentBlock {

    public ConfluenceMacro(BasedSequence basedSequence) {
        super(basedSequence);
    }
}
