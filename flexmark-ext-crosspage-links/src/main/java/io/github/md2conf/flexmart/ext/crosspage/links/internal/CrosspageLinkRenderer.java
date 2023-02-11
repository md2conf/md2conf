package io.github.md2conf.flexmart.ext.crosspage.links.internal;

import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import io.github.md2conf.converter.ExtractTitleStrategy;
import io.github.md2conf.converter.TitleExtractor;
import io.github.md2conf.flexmart.ext.crosspage.links.CrosspageLink;
import io.github.md2conf.flexmart.ext.crosspage.links.CrosspageLinkExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static io.github.md2conf.converter.ExtractTitleStrategy.FROM_FILENAME;

public class CrosspageLinkRenderer implements NodeRenderer{
    private final ExtractTitleStrategy extractTitleStrategy;

    public CrosspageLinkRenderer(ExtractTitleStrategy extractTitleStrategy) {
        this.extractTitleStrategy = extractTitleStrategy;
    }

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
            String title = extractTitle(node);
            html.raw(title);
            html.raw("]");
        }
    }

    private String extractTitle(CrosspageLink node) {
        switch (extractTitleStrategy){
            case FROM_FILENAME:
                return TitleExtractor.extractTitle(node.getPath(), FROM_FILENAME);
            case FROM_FIRST_HEADER:
                return FlexmarkTitleExtractor.extractTitle(node.getPath());
            default:
                throw new IllegalArgumentException();
        }
    }

    public static class Factory implements NodeRendererFactory {

        @NotNull
        public NodeRenderer apply(@NotNull DataHolder options) {
            ExtractTitleStrategy extractTitleStrategy = (ExtractTitleStrategy) options.getAll()
                    .get(CrosspageLinkExtension.EXTRACT_TITLE_STRATEGY);
            return new CrosspageLinkRenderer(extractTitleStrategy);
        }
    }
}
