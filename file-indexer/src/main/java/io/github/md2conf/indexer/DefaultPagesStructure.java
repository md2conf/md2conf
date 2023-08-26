package io.github.md2conf.indexer;

import java.util.List;

public class DefaultPagesStructure implements PagesStructure {
    private final List<DefaultPage> pages;

    public DefaultPagesStructure(List<DefaultPage> pages) {
        this.pages = pages;
    }

    @Override
    public List<? extends Page> pages() {
        return pages;
    }
}
