package io.github.md2conf.converter.noop;

import io.github.md2conf.converter.AttachmentUtil;
import io.github.md2conf.converter.ConfluencePageFactory;
import io.github.md2conf.converter.Converter;
import io.github.md2conf.indexer.Page;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;

import java.util.ArrayList;
import java.util.List;

public class NoopConverter implements Converter {

    private final ConfluencePageFactory confluencePageFactory;

    public NoopConverter(ConfluencePageFactory confluencePageFactory) {
        this.confluencePageFactory = confluencePageFactory;
    }

    @Override
    public ConfluenceContentModel convert(PagesStructure pagesStructure) {
        List<ConfluencePage> confluencePages = new ArrayList<>();
        for (Page topLevelPage : pagesStructure.pages()) { //use "for" loop to throw exception to caller
            ConfluencePage confluencePage = createConfluencePage(topLevelPage);
            confluencePages.add(confluencePage);
        }
        return new ConfluenceContentModel(confluencePages);
    }

    private ConfluencePage createConfluencePage(Page defaultPage) {
        ConfluencePage result = confluencePageFactory.pageByPath(defaultPage.path());
        result.setAttachments(AttachmentUtil.toAttachmentsMap(defaultPage.attachments()));
        for (Page childPage : defaultPage.children()) {
            ConfluencePage childConfluencePage = createConfluencePage(childPage);
            result.getChildren().add(childConfluencePage);
        }
        return result;
    }
}
