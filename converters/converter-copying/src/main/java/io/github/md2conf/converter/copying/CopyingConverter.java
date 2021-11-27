package io.github.md2conf.converter.copying;

import io.github.md2conf.converter.AttachmentUtil;
import io.github.md2conf.converter.ConfluencePageFactory;
import io.github.md2conf.converter.Converter;
import io.github.md2conf.indexer.Page;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class CopyingConverter implements Converter {

    private final ConfluencePageFactory confluencePageFactory;
    private final Path outputPath;


    public CopyingConverter(ConfluencePageFactory confluencePageFactory, Path outputPath) {
        this.confluencePageFactory = confluencePageFactory;
        this.outputPath = outputPath;
    }

    @Override
    public ConfluenceContentModel convert(PagesStructure pagesStructure) throws IOException {
        List<ConfluencePage> confluencePages = new ArrayList<>();
        for (Page topLevelPage : pagesStructure.pages()) { //use "for" loop to throw exception to caller
            ConfluencePage confluencePage;
            confluencePage = copyAndCreateConfluencePage(topLevelPage, Paths.get(""));
            confluencePages.add(confluencePage);
        }
        return new ConfluenceContentModel(confluencePages);
    }

    private ConfluencePage copyAndCreateConfluencePage(Page page, Path relativePart) throws IOException {
        //copy
        Path targetPath = PathUtils.copyFileToDirectory(page.path(), outputPath.resolve(relativePart));
        List<Path> copiedAttachments = AttachmentUtil.copyPageAttachments(page.attachments(), targetPath);
        // create ConfluencePage
        ConfluencePage result = confluencePageFactory.pageByPath(targetPath);
        result.setAttachments(AttachmentUtil.toAttachmentsMap(copiedAttachments));
        if (page.children() != null && !page.children().isEmpty()) {
            String childrenDirAsStr = FilenameUtils.concat(
                    relativePart.toString(),
                    FilenameUtils.removeExtension(targetPath.getFileName().toString()));
            Path childrenDir = outputPath.resolve(childrenDirAsStr);
            if (!childrenDir.toFile().mkdirs()) {
                throw new IOException("Cannot create dirs in " + childrenDir);
            }
            for (Page childPage : page.children()) {
                result.getChildren().add(copyAndCreateConfluencePage(childPage, outputPath.relativize(childrenDir)));
            }
        }
        return result;
    }

}
