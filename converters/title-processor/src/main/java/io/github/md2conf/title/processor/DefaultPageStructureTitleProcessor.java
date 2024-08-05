package io.github.md2conf.title.processor;

import io.github.md2conf.indexer.Page;
import io.github.md2conf.indexer.PagesStructure;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DefaultPageStructureTitleProcessor implements PageStructureTitleProcessor {

    private final TitleExtractor titleExtractor;
    private final String titlePrefix;
    private final String titleSuffix;
    private final boolean titleChildPrefixed;

    @Deprecated
    public DefaultPageStructureTitleProcessor(TitleExtractStrategy titleExtractStrategy,
                                              String titlePrefix,
                                              String titleSuffix,
                                              boolean titleChildPrefixed) {
        this.titleExtractor = new DefaultTitleExtractor(titleExtractStrategy);
        this.titlePrefix = titlePrefix;
        this.titleSuffix = titleSuffix;
        this.titleChildPrefixed = titleChildPrefixed;
    }

    public DefaultPageStructureTitleProcessor(TitleProcessorOptions titleProcessorOptions){
        this.titleExtractor = new DefaultTitleExtractor(titleProcessorOptions.getTitleExtractStrategy());
        this.titlePrefix = titleProcessorOptions.getTitlePrefix();
        this.titleSuffix = titleProcessorOptions.getTitleSuffix();
        this.titleChildPrefixed = titleProcessorOptions.isTitleChildPrefixed();
    }

    @Override
    public Map<Path, String> toTitleMap(PagesStructure pagesStructure) throws IOException {
        HashMap<Path, String> res = new HashMap<>();
        for (Page page : pagesStructure.pages()) {
            titleExtractAndFormat(res, page, titlePrefix, titleSuffix, true);
        }
        return res;
    }

    private void titleExtractAndFormat(HashMap<Path, String> hashMap, Page page, String titlePrefix, String titleSuffix, boolean isRoot) throws IOException {
        String extracted = titleExtractor.extractTitle(page.path());
        StringBuilder sb = new StringBuilder();
        if (titlePrefix != null && !titlePrefix.isBlank() && !(isRoot && titleChildPrefixed)) {
            sb.append(titlePrefix).append(" - ");
        }
        sb.append(extracted);
        if (titleSuffix != null && !titleSuffix.isBlank() && !(isRoot && titleChildPrefixed)) {
            sb.append(" - ").append(titleSuffix);
        }
        hashMap.put(page.path().normalize().toAbsolutePath(), sb.toString());
        for (Page child : page.children()) {
            String childPrefix = isRoot && titleChildPrefixed ? extracted : titlePrefix;
            String childSuffix = isRoot && titleChildPrefixed ? null : titleSuffix;
            titleExtractAndFormat(hashMap, child, childPrefix, childSuffix, false);
        }
    }

}
