package io.github.md2conf.flexmart.ext.confluence.macros.internal;

import com.vladsch.flexmark.ast.HtmlCommentBlock;
import com.vladsch.flexmark.ast.HtmlInlineComment;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeTracker;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import io.github.md2conf.flexmart.ext.confluence.macros.ConfluenceMacro;
import org.jetbrains.annotations.NotNull;

public class ConfluenceMacroPostProcessor extends NodePostProcessor {

    @Override
    public void process(@NotNull NodeTracker state, @NotNull Node node) {
        if ((node instanceof HtmlCommentBlock) || (node instanceof HtmlInlineComment)) {
            String text = node.getChars().toString();
            int startPos = text.indexOf('{');
            int endPos = text.lastIndexOf('}');
            if (startPos > 1 && endPos > 1) {
                Node parent = node.getParent();
                Node prev = node.getPrevious();
                Node next = node.getNext();
                String macroText = text.substring(startPos, endPos+1);
                ConfluenceMacro confluenceMacro = new ConfluenceMacro(BasedSequence.of(macroText), node instanceof HtmlCommentBlock);
                node.unlink();
                if (parent != null) {
                    if (prev!=null){
                        prev.insertAfter(confluenceMacro);
                    }
                    else if (next!=null){
                        next.insertBefore(confluenceMacro);
                    }
                    else {
                        parent.appendChild(confluenceMacro);
                    }
                }
                state.nodeRemoved(node);
                state.nodeAddedWithChildren(confluenceMacro);
            }
        }
    }

    public static class Factory extends NodePostProcessorFactory {
        public Factory() {
            super(false);
            addNodes(HtmlCommentBlock.class,HtmlInlineComment.class);
        }

        @Override
        public @NotNull NodePostProcessor apply(@NotNull Document document) {
            return new ConfluenceMacroPostProcessor();
        }
    }
}
