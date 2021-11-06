package io.github.md2conf.converter.markdown;

import io.github.md2conf.indexer.PagesStructureProvider;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MarkdownPage implements PagesStructureProvider.Page {

    private final Path path;
    private final List<MarkdownPage> children;

    public MarkdownPage(Path path) {
        this.path = path;
        this.children = new ArrayList<>();
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public List<MarkdownPage> children() {
        return children;
    }

    void addChild(MarkdownPage child) {
        this.children.add(child);
    }
}
