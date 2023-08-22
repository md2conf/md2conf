package io.github.md2conf.indexer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class DefaultPage implements Page {

    private final Path path;
    private final List<DefaultPage> children;

    private final List<Path> attachments;

    public DefaultPage(Path path) {
        this.path = path;
        this.children = new ArrayList<>();
        attachments = new ArrayList<>();
    }

    public DefaultPage(Path path, List<Path> attachments) {
        this.path = path;
        this.children = new ArrayList<>();
        this.attachments = attachments;
    }

    public DefaultPage(Path path, List<DefaultPage> children, List<Path> attachments) {
        this.path = path;
        this.children = children;
        this.attachments = attachments;
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public List<Page> children() {
        return unmodifiableList(this.children);
    }

    @Override
    public List<Path> attachments() {
        return attachments;
    }

    public void addChild(DefaultPage page) {
        this.children.add(page);
    }
}
