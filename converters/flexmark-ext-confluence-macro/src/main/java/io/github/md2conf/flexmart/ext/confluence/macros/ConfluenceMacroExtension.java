package io.github.md2conf.flexmart.ext.confluence.macros;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import io.github.md2conf.flexmart.ext.confluence.macros.internal.ConfluenceMacroPostProcessor;
import io.github.md2conf.flexmart.ext.confluence.macros.internal.ConfluenceMacroRenderer;
import org.jetbrains.annotations.NotNull;

public class ConfluenceMacroExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

    public static ConfluenceMacroExtension create() {
        return new ConfluenceMacroExtension();
    }

    @Override
    public void rendererOptions(@NotNull MutableDataHolder options) {
    }

    @Override
    public void extend(HtmlRenderer.@NotNull Builder htmlRendererBuilder, @NotNull String rendererType) {
        if (htmlRendererBuilder.isRendererType("HTML")) {
        } else if (htmlRendererBuilder.isRendererType("JIRA")) {
            htmlRendererBuilder.nodeRendererFactory(new ConfluenceMacroRenderer.Factory());
        }
    }

    public void parserOptions(MutableDataHolder options) {
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessorFactory(new ConfluenceMacroPostProcessor.Factory());
    }
}