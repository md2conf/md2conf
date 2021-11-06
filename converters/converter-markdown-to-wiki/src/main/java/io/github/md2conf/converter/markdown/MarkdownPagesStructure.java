package io.github.md2conf.converter.markdown;

import io.github.md2conf.indexer.PagesStructureProvider;

import java.util.List;

public class MarkdownPagesStructure implements PagesStructureProvider.PagesStructure {
    List<MarkdownPage> markdownPages;

    public MarkdownPagesStructure(List<MarkdownPage> markdownPages) {
        this.markdownPages = markdownPages;
    }

    @Override
    public List<MarkdownPage> pages() {
        return markdownPages;
    }
}
