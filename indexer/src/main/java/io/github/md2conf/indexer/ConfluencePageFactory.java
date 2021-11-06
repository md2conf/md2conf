package io.github.md2conf.indexer;

import io.github.md2conf.model.ConfluencePage;

import java.io.IOException;
import java.nio.file.Path;

public class ConfluencePageFactory {


    private final ExtractTitleStrategy extractTitleStrategy;

    public ConfluencePageFactory(ExtractTitleStrategy extractTitleStrategy) {
        this.extractTitleStrategy = extractTitleStrategy;
    }

    /**
     *  Create ConfluencePage by given path.
     *  Doesn't set children.
     *
     * @param path file path with content
     * @return ConfluencePage
     */
    public ConfluencePage pageByPath(Path path) throws IOException {
        ConfluencePage page = new ConfluencePage();
        page.setContentFilePath(path.toFile().getAbsolutePath());
        page.setTitle(TitleExtractor.extractTitle(path, extractTitleStrategy));

        return page;
    }


}
