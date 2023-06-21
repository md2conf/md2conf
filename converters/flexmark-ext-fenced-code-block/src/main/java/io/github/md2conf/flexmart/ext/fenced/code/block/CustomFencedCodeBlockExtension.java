package io.github.md2conf.flexmart.ext.fenced.code.block;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import io.github.md2conf.flexmart.ext.fenced.code.block.internal.CustomFencedCodeBlockPostProcessor;
import io.github.md2conf.flexmart.ext.fenced.code.block.internal.CustomFencedCodeBlockRenderer;
import org.jetbrains.annotations.NotNull;

public class CustomFencedCodeBlockExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension{

    public static CustomFencedCodeBlockExtension create() {
        return new CustomFencedCodeBlockExtension();
    }

    @Override
    public void rendererOptions(@NotNull MutableDataHolder mutableDataHolder) {

    }

    @Override
    public void extend(HtmlRenderer.@NotNull Builder htmlRendererBuilder, @NotNull String rendererType) {
        if (htmlRendererBuilder.isRendererType("HTML")) {
        } else if (htmlRendererBuilder.isRendererType("JIRA")) {
            htmlRendererBuilder.nodeRendererFactory(new CustomFencedCodeBlockRenderer.Factory());
        }
    }

    @Override
    public void parserOptions(MutableDataHolder mutableDataHolder) {

    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessorFactory(new CustomFencedCodeBlockPostProcessor.Factory());
    }
}
