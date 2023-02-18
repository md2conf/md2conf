package io.github.md2conf.flexmart.ext.crosspage.links.internal;

import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import io.github.md2conf.flexmart.ext.crosspage.links.CrosspageLink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class CrosspageLinkRenderer implements NodeRenderer{

    @Override
    public @Nullable Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<>();
        set.add(new NodeRenderingHandler<>(CrosspageLink.class, this::render));
        return set;
    }

    private void render(CrosspageLink node, NodeRendererContext context, HtmlWriter html) {
        if (context.isDoNotRenderLinks()) {
            context.renderChildren(node);
        } else {
            html.raw("[");
            if (node.getText() != null
                    && !node.getText().isEmpty()
                    && !node.getText().equals(node.getUrl())) {
                html.raw(node.getText()).raw("|");
            }
            html.raw(node.getTitle());
            html.raw("]");
        }
    }


    public static class Factory implements NodeRendererFactory {
        @NotNull
        public NodeRenderer apply(@NotNull DataHolder options) {
            return new CrosspageLinkRenderer();
        }
    }
}
