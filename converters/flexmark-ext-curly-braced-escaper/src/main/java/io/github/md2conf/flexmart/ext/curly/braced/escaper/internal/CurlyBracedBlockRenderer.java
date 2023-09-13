package io.github.md2conf.flexmart.ext.curly.braced.escaper.internal;

import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import io.github.md2conf.flexmart.ext.curly.braced.escaper.CurlyBracedBlock;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CurlyBracedBlockRenderer implements NodeRenderer {
    public CurlyBracedBlockRenderer(DataHolder options) {

    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<>();
        set.add(new NodeRenderingHandler<>(CurlyBracedBlock.class, this::render));
        return set;
    }

    private void render(CurlyBracedBlock node, NodeRendererContext context, HtmlWriter html) {
        html.raw('\\' + node.getOpeningMarker().toString() ); // \{
        context.renderChildren(node);
        html.raw('\\' + node.getClosingMarker().toString() ); // \}
    }

    public static class Factory implements NodeRendererFactory {
        @NotNull
        @Override
        public NodeRenderer apply(@NotNull DataHolder options) {
            return new CurlyBracedBlockRenderer(options);
        }
    }
}
