package io.github.md2conf.flexmart.ext.local.attachments;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import io.github.md2conf.flexmart.ext.local.attachments.internal.LocalAttachmentLinkPostProcessor;
import io.github.md2conf.flexmart.ext.local.attachments.internal.LocalAttachmentLinkRenderer;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalAttachmentLinkExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension{

    /**
     * Path against local attachment link will be resolved
     */
    final public static DataKey<Path> CURRENT_FILE_PATH = new DataKey<>("CURRENT_FILE_PATH", Paths.get(""));
    public static LocalAttachmentLinkExtension create() {
        return new LocalAttachmentLinkExtension();
    }

    public void rendererOptions(@NotNull MutableDataHolder options) {

    }

    @Override
    public void extend(HtmlRenderer.@NotNull Builder htmlRendererBuilder, @NotNull String rendererType) {
        if (htmlRendererBuilder.isRendererType("HTML")) {
        } else if (htmlRendererBuilder.isRendererType("JIRA")) {
            htmlRendererBuilder.nodeRendererFactory(new LocalAttachmentLinkRenderer.Factory());
        }
    }


    public void parserOptions(MutableDataHolder options) {

    }
    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessorFactory(new LocalAttachmentLinkPostProcessor.Factory(parserBuilder));
    }
}
