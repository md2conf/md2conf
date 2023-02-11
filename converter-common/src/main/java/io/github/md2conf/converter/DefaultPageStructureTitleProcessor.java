package io.github.md2conf.converter;

import io.github.md2conf.indexer.PagesStructure;

import java.nio.file.Path;
import java.util.Map;

public class DefaultPageStructureTitleProcessor implements PageStructureTitleProcessor {

    private final ExtractTitleStrategy titleExtract;
    private final String titlePrefix;
    private final String titleSuffix;
    private final boolean titleChildPrefixed;

    public DefaultPageStructureTitleProcessor(ExtractTitleStrategy titleExtract, String titlePrefix, String titleSuffix, boolean titleChildPrefixed) {
        this.titleExtract = titleExtract;
        this.titlePrefix = titlePrefix;
        this.titleSuffix = titleSuffix;
        this.titleChildPrefixed = titleChildPrefixed;
    }

    @Override
    public Map<Path, String> toTitleMap(PagesStructure pagesStructure) {
        return null;
    }
}
