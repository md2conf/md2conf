package io.github.md2conf.flexmart.ext.curly.braced.escaper.internal;

import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.Escaping;
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
        set.add(new NodeRenderingHandler<>(Code.class, this::render));
        return set;
    }

    private void render(CurlyBracedBlock node, NodeRendererContext context, HtmlWriter html) {
        html.raw('\\' + node.getOpeningMarker().toString() ); // \{
        context.renderChildren(node);
        html.raw('\\' + node.getClosingMarker().toString() ); // \}
    }

    // Overrides the JIRA converter's inline code rendering (which emits {{...}} verbatim).
    // Inline code is always literal text, but Confluence still scans it for macro ({...}) and
    // link ([...]) syntax, which throws UnknownMacroMigrationException on publish or misrenders.
    // Curly and square braces are therefore encoded as HTML entities so they render as-is.
    // NB: takes effect only because CurlyBracedBlockExtension is registered AFTER
    // JiraConverterExtension (the last-registered node renderer for a type wins in flexmark).
    private void render(Code node, NodeRendererContext context, HtmlWriter html) {
        html.raw("{{");
        html.raw(encodeBraces(Escaping.collapseWhitespace(node.getText(), true)));
        html.raw("}}");
    }

    private static String encodeBraces(CharSequence text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '{':  sb.append("&#123;"); break;
                case '}':  sb.append("&#125;"); break;
                case '[':  sb.append("&#91;");  break;
                case ']':  sb.append("&#93;");  break;
                default:   sb.append(c);
            }
        }
        return sb.toString();
    }

    public static class Factory implements NodeRendererFactory {
        @NotNull
        @Override
        public NodeRenderer apply(@NotNull DataHolder options) {
            return new CurlyBracedBlockRenderer(options);
        }
    }
}
