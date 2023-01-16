package io.github.md2conf.converter.md2wiki.ext;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeTracker;
import com.vladsch.flexmark.util.data.DataHolder;
import org.jetbrains.annotations.NotNull;

public class AttachmentLinkPostProcessor extends NodePostProcessor {
    static class Factory extends NodePostProcessorFactory {

        public Factory(DataHolder options) {
            super(false);
            addNodes(Link.class);
        }

        @NotNull
        @Override
        public NodePostProcessor apply(@NotNull Document document) {
            return new AttachmentLinkPostProcessor();
        }
    }

    @Override
    public void process(@NotNull NodeTracker state, @NotNull Node node) {
        Link link = (Link) node;
        Text text = new Text("{attachment|"+link.getText() +"}"); //todo implement right text here
        link.insertAfter(text);
        state.nodeAdded(text);

        link.unlink();
        state.nodeRemoved(link);
    }
}
