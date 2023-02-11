package io.github.md2conf.flexmart.ext.crosspage.links;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import io.github.md2conf.converter.ExtractTitleStrategy;
import io.github.md2conf.flexmart.ext.crosspage.links.internal.CrosspageLinkPostProcessor;
import io.github.md2conf.flexmart.ext.crosspage.links.internal.CrosspageLinkRenderer;
import io.github.md2conf.indexer.DefaultFileIndexer.DefaultPagesStructure;
import io.github.md2conf.indexer.PagesStructure;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CrosspageLinkExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension{

    /**
     * Path against local links will be resolved
     */
    final public static DataKey<Path> CURRENT_FILE_PATH = new DataKey<>("CURRENT_FILE_PATH", Paths.get(""));
    /**
     * Pages structure
     */
    final public static DataKey<PagesStructure> PAGES_STRUCTURE = new DataKey<>("PAGES_STRUCTURE", new DefaultPagesStructure(List.of()));
    final public static DataKey<ExtractTitleStrategy> EXTRACT_TITLE_STRATEGY = new DataKey<>("PAGES_STRUCTURE", ExtractTitleStrategy.FROM_FILENAME);

    public static CrosspageLinkExtension create() {
        return new CrosspageLinkExtension();
    }

    public void rendererOptions(@NotNull MutableDataHolder options) {

    }

    @Override
    public void extend(HtmlRenderer.@NotNull Builder htmlRendererBuilder, @NotNull String rendererType) {
        if (htmlRendererBuilder.isRendererType("HTML")) {
        } else if (htmlRendererBuilder.isRendererType("JIRA")) {
            htmlRendererBuilder.nodeRendererFactory(new CrosspageLinkRenderer.Factory());
        }
    }


    public void parserOptions(MutableDataHolder options) {

    }
    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessorFactory(new CrosspageLinkPostProcessor.Factory(parserBuilder));
    }
}
