package io.github.md2conf.flexmart.ext.local.image;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import io.github.md2conf.flexmart.ext.local.image.internal.LocalImagePostProcessor;
import io.github.md2conf.flexmart.ext.local.image.internal.LocalImageRenderer;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalImageExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension{

    /**
     * Path against local attachment link will be resolved
     */
    public static final DataKey<Path> CURRENT_FILE_PATH = new DataKey<>("CURRENT_FILE_PATH", Paths.get(""));
    public static LocalImageExtension create() {
        return new LocalImageExtension();
    }

    public void rendererOptions(@NotNull MutableDataHolder options) {

    }

    @Override
    public void extend(HtmlRenderer.@NotNull Builder htmlRendererBuilder, @NotNull String rendererType) {
        if (htmlRendererBuilder.isRendererType("HTML")) {
        } else if (htmlRendererBuilder.isRendererType("JIRA")) {
            htmlRendererBuilder.nodeRendererFactory(new LocalImageRenderer.Factory());
        }
    }

    public void parserOptions(MutableDataHolder options) {

    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessorFactory(new LocalImagePostProcessor.Factory(parserBuilder));
    }

}
