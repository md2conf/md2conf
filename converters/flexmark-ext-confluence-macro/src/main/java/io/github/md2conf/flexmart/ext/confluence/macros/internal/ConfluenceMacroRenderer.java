package io.github.md2conf.flexmart.ext.confluence.macros.internal;

import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.SequenceUtils;
import io.github.md2conf.flexmart.ext.confluence.macros.ConfluenceMacro;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ConfluenceMacroRenderer implements NodeRenderer {
    @Override
    public @Nullable Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<>();
        set.add(new NodeRenderingHandler<>(ConfluenceMacro.class, this::render));
        return set;
    }

    private void render(ConfluenceMacro node, NodeRendererContext context, HtmlWriter html) {
            html.raw(node.getChars());
            if (node.isWithEOL()) {
                html.raw(SequenceUtils.EOL);
            }
    }

    public static class Factory implements NodeRendererFactory {
        @NotNull
        public NodeRenderer apply(@NotNull DataHolder options) {
            return new ConfluenceMacroRenderer();
        }
    }
}
