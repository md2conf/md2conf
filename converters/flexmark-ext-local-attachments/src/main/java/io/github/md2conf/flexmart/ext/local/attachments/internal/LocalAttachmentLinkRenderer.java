package io.github.md2conf.flexmart.ext.local.attachments.internal;

import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.*;
import com.vladsch.flexmark.util.data.DataHolder;
import io.github.md2conf.flexmart.ext.local.attachments.LocalAttachmentLink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class LocalAttachmentLinkRenderer implements NodeRenderer {

    public LocalAttachmentLinkRenderer(DataHolder options) {
    }

    public @Nullable Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<>();
        set.add(new NodeRenderingHandler<>(LocalAttachmentLink.class, this::render));
        return set;
    }
    private void render(LocalAttachmentLink node, NodeRendererContext context, HtmlWriter html) {
        if (context.isDoNotRenderLinks()) {
            context.renderChildren(node);
        } else {
            html.raw("[");
            if (node.getText() != null) {
                html.raw(node.getText()).raw("|");
            }
            html.raw("^");
            String fileName = node.getPath().getFileName().toString();
            html.raw(fileName);
            if (node.getTitle() != null && !node.getTitle().isEmpty()) {
                html.raw("|").raw(node.getTitle());
            }
            html.raw("]");
        }
    }


    public static class Factory implements NodeRendererFactory {
        @NotNull
        public NodeRenderer apply(@NotNull DataHolder options) {
            return new LocalAttachmentLinkRenderer(options);
        }
    }
}
