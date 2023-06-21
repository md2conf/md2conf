package io.github.md2conf.flexmart.ext.fenced.code.block.internal;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeTracker;
import io.github.md2conf.flexmart.ext.fenced.code.block.CustomFencedCodeBlock;
import org.jetbrains.annotations.NotNull;

public class CustomFencedCodeBlockPostProcessor extends NodePostProcessor {

    @Override
    public void process(@NotNull NodeTracker state, @NotNull Node node) {
        if (node instanceof FencedCodeBlock) {
            Node previous = node.getPrevious();
            Node parent = node.getParent();
            CustomFencedCodeBlock customFencedCodeBlock =
                    new CustomFencedCodeBlock(((FencedCodeBlock) node).getContentChars(),
                            ((FencedCodeBlock) node).getInfo());
            node.unlink();
            if (previous != null) {
                previous.insertAfter(customFencedCodeBlock);
            } else if (parent != null) {
                parent.appendChild(customFencedCodeBlock);
            }
            state.nodeRemoved(node);
            state.nodeAddedWithChildren(customFencedCodeBlock);
        }
    }

    public static class Factory extends NodePostProcessorFactory {
        public Factory() {
            super(false);
            addNodes(FencedCodeBlock.class);
        }

        @Override
        public @NotNull NodePostProcessor apply(@NotNull Document document) {
            return new CustomFencedCodeBlockPostProcessor();
        }
    }
}
