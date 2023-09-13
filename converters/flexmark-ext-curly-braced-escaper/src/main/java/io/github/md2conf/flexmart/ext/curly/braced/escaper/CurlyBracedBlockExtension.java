package io.github.md2conf.flexmart.ext.curly.braced.escaper;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import io.github.md2conf.flexmart.ext.curly.braced.escaper.internal.CurlyBracedBlockProcessor;
import io.github.md2conf.flexmart.ext.curly.braced.escaper.internal.CurlyBracedBlockRenderer;
import org.jetbrains.annotations.NotNull;

public class CurlyBracedBlockExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

    public static CurlyBracedBlockExtension create() {
        return new CurlyBracedBlockExtension();
    }

    @Override
    public void rendererOptions(@NotNull MutableDataHolder options) {
    }

    @Override
    public void extend(HtmlRenderer.@NotNull Builder htmlRendererBuilder, @NotNull String rendererType) {
        if (htmlRendererBuilder.isRendererType("HTML")) {
        } else if (htmlRendererBuilder.isRendererType("JIRA")) {
            htmlRendererBuilder.nodeRendererFactory(new CurlyBracedBlockRenderer.Factory());
        }
    }

    public void parserOptions(MutableDataHolder options) {
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customDelimiterProcessor(new CurlyBracedBlockProcessor());
    }
}