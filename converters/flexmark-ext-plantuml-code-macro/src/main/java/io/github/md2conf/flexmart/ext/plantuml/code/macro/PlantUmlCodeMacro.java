package io.github.md2conf.flexmart.ext.plantuml.code.macro;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class PlantUmlCodeMacro extends FencedCodeBlock {

    public PlantUmlCodeMacro(BasedSequence chars) {
        super(chars);
    }
}
