package io.github.md2conf.flexmart.ext.plantuml.code.macro;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import io.github.md2conf.flexmart.ext.plantuml.code.macro.internal.PlantUmlCodeMacroPostProcessor;
import io.github.md2conf.flexmart.ext.plantuml.code.macro.internal.PlantUmlCodeMacroRenderer;
import org.jetbrains.annotations.NotNull;

public class PlantUmlCodeMacroExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

    public static PlantUmlCodeMacroExtension create() {
        return new PlantUmlCodeMacroExtension();
    }

    @Override
    public void rendererOptions(@NotNull MutableDataHolder mutableDataHolder) {

    }

    @Override
    public void extend(HtmlRenderer.@NotNull Builder htmlRendererBuilder, @NotNull String rendererType) {
        if (htmlRendererBuilder.isRendererType("HTML")) {
        } else if (htmlRendererBuilder.isRendererType("JIRA")) {
            htmlRendererBuilder.nodeRendererFactory(new PlantUmlCodeMacroRenderer.Factory());
        }
    }

    @Override
    public void parserOptions(MutableDataHolder mutableDataHolder) {

    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessorFactory(new PlantUmlCodeMacroPostProcessor.Factory());
    }
}
