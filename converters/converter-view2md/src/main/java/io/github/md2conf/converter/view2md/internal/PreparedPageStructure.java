package io.github.md2conf.converter.view2md.internal;

import java.util.List;

public class PreparedPageStructure {
    private final List<PreparedPage> pages;

    public PreparedPageStructure(List<PreparedPage> pages) {
        this.pages = pages;
    }

    public List<PreparedPage> getPages() {
        return pages;
    }
}
