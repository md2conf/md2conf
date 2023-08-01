package io.github.md2conf.flexmart.ext.plantuml.code.macro.internal;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeTracker;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import io.github.md2conf.flexmart.ext.plantuml.code.macro.PlantUmlCodeMacro;
import org.jetbrains.annotations.NotNull;

public class PlantUmlCodeMacroPostProcessor extends NodePostProcessor {

    @Override
    public void process(@NotNull NodeTracker state, @NotNull Node node) {
        if (node instanceof FencedCodeBlock) {
            Node previous = node.getPrevious();

            String info = ((FencedCodeBlock) node).getInfo().toString();
            if (info.equals("plantuml") || info.equals("puml") || info.equals("c4plantuml")) {
                Node parent = node.getParent();
                Node prev = node.getPrevious();
                Node next = node.getNext();
                BasedSequence text = ((FencedCodeBlock) node).getContentChars();
                PlantUmlCodeMacro plantUmlCodeMacro = new PlantUmlCodeMacro(text);
                node.unlink();
                if (parent != null) {
                    if (prev!=null){
                        prev.insertAfter(plantUmlCodeMacro);
                    }
                    else if (next!=null){
                        next.insertBefore(plantUmlCodeMacro);
                    }
                    else {
                        parent.appendChild(plantUmlCodeMacro);
                    }
                }
                state.nodeRemoved(node);
                state.nodeAddedWithChildren(plantUmlCodeMacro);
            }
        }
    }

    public static class Factory extends NodePostProcessorFactory {
        public Factory() {
            super(false);
            addNodes(FencedCodeBlock.class);
        }

        @Override
        public @NotNull NodePostProcessor apply(@NotNull Document document) {
            return new PlantUmlCodeMacroPostProcessor();
        }
    }
}
