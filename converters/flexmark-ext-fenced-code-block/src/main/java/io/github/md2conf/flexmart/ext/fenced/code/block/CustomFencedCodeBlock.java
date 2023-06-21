package io.github.md2conf.flexmart.ext.fenced.code.block;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class CustomFencedCodeBlock extends FencedCodeBlock {
    public CustomFencedCodeBlock(BasedSequence chars, BasedSequence info) {
        super(chars);
        setInfo(info);
    }
}
