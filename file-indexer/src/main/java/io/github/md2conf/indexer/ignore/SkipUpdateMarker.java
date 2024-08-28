package io.github.md2conf.indexer.ignore;

import io.github.md2conf.indexer.DefaultPage;
import io.github.md2conf.indexer.Page;
import io.github.md2conf.indexer.PagesStructure;
import org.eclipse.jgit.ignore.IgnoreNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class SkipUpdateMarker {

    public static final String DEFAULT_SKIPUPDATE_FILE = ".md2conf_skipupdate";

    private final IgnoreNode ignoreNode;
    private final Path basePath;

    public SkipUpdateMarker(Path basePath) {
        this.basePath = basePath;
        this.ignoreNode = new IgnoreNode();
        init(this.ignoreNode);
    }


    public void visitAndMark(PagesStructure pagesStructure) {
        for (Page page : pagesStructure.pages()) {
            markIfNecessary(page);
        }
    }

    protected void init(IgnoreNode ignoreNode) {
        File defaultFile = new File(basePath.toFile(), DEFAULT_SKIPUPDATE_FILE);
        if (defaultFile.exists()) {
            try {
                loadRulesFromFile(ignoreNode, defaultFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void markIfNecessary(Page page) {
        if (needtoSkipUpdate(page)) {
            ((DefaultPage) page).setSkipUpdate(true);
        }
        for (Page child : page.children()) {
            markIfNecessary(child);
        }
    }

    private boolean needtoSkipUpdate(Page page) {
        return Boolean.TRUE.equals(
                ignoreNode.checkIgnored(basePath.relativize(page.path()).toString(), false));
    }

    private static void loadRulesFromFile(IgnoreNode r, File exclude)
            throws IOException {
        try (FileInputStream in = new FileInputStream(exclude)) {
            r.parse(exclude.getAbsolutePath(), in);
        }
    }
}
