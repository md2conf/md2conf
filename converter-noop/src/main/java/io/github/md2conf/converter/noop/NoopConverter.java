package io.github.md2conf.converter.noop;

import io.github.md2conf.converter.AttachmentUtil;
import io.github.md2conf.converter.Converter;
import io.github.md2conf.converter.PageStructureTitleProcessor;
import io.github.md2conf.indexer.Page;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoopConverter implements Converter {

    private final PageStructureTitleProcessor pageStructureTitleProcessor;

    public NoopConverter(PageStructureTitleProcessor pageStructureTitleProcessor) {
        this.pageStructureTitleProcessor = pageStructureTitleProcessor;
    }

    @Override
    public ConfluenceContentModel convert(PagesStructure pagesStructure) {
        Map<Path,String> titleMap = pageStructureTitleProcessor.toTitleMap(pagesStructure);

        List<ConfluencePage> confluencePages = new ArrayList<>();
        for (Page topLevelPage : pagesStructure.pages()) { //use "for" loop to throw exception to caller
            ConfluencePage confluencePage = createConfluencePage(topLevelPage, titleMap);
            confluencePages.add(confluencePage);
        }
        return new ConfluenceContentModel(confluencePages);
    }

    private ConfluencePage createConfluencePage(Page defaultPage, Map<Path, String> titleMap) {
        ConfluencePage result = new ConfluencePage();
        result.setContentFilePath(defaultPage.path().toString());
        result.setTitle(titleMap.get(defaultPage.path()));
        result.setAttachments(AttachmentUtil.toAttachmentsMap(defaultPage.attachments()));
        for (Page childPage : defaultPage.children()) {
            ConfluencePage childConfluencePage = createConfluencePage(childPage, titleMap);
            result.getChildren().add(childConfluencePage);
        }
        return result;
    }
}
