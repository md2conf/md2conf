package io.github.md2conf.flexmart.ext.local.image.internal;

import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import io.github.md2conf.flexmart.ext.local.image.LocalImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class LocalImageRenderer implements NodeRenderer {

    public LocalImageRenderer(DataHolder options) {
    }

    @Override
    public @Nullable Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        return new HashSet<>(List.of(
                new NodeRenderingHandler<>(LocalImage.class, this::render)
        ));
    }


    private void render(LocalImage node, NodeRendererContext context, HtmlWriter html) {
        if (!context.isDoNotRenderLinks()) {
            html.raw("!").raw(node.getFileName());
            if (hasTitle(node)|| hasDescription(node)){
                html.raw("|");
            }
            if (hasTitle(node)) {
                html.raw("title=").raw(node.getTitle());
            }
            if (hasDescription(node)){
                if (hasTitle(node)){
                    html.raw(", ");
                }
                html.raw("alt=").raw(node.getText());
            }
            html.raw("!");
        }
    }

    private static boolean hasDescription(LocalImage node) {
        return node.getText() != null && !node.getText().isEmpty() && !Objects.equals(node.getText().toString(), node.getFileName());
    }

    private static boolean hasTitle(LocalImage node) {
        return node.getTitle() != null && !node.getTitle().isEmpty();
    }

    public static class Factory implements NodeRendererFactory {
        @NotNull
        @Override
        public NodeRenderer apply(@NotNull DataHolder options) {
            return new LocalImageRenderer(options);
        }
    }
}
