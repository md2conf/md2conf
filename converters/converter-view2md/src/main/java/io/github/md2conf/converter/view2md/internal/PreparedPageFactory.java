package io.github.md2conf.converter.view2md.internal;

import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static io.github.md2conf.converter.view2md.FileNameUtil.sanitizeFileName;

public class PreparedPageFactory {


    public static PreparedPageStructure fromModel(ConfluenceContentModel model, Path outputDir) {
        List<PreparedPage> preparedPages = new ArrayList<>();
        for (ConfluencePage page : model.getPages()) {
            preparedPages.add(convertPage(page, outputDir));
        }
        return new PreparedPageStructure(preparedPages);
    }

    private static PreparedPage convertPage(ConfluencePage page, Path outputDir) {
        PreparedPage preparedPage = new PreparedPage();
        preparedPage.setPageTitle(page.getTitle());
        preparedPage.setSourcePath(Path.of(page.getContentFilePath()));
        preparedPage.setAttachments(page.getAttachments());

        var resName = sanitizeFileName(page.getTitle()+".md");
        Path targetPath =  outputDir.resolve(resName);
        preparedPage.setTargetPath(targetPath);

        List<PreparedPage> childernPages = new ArrayList<>();
        for (ConfluencePage confluencePage: page.getChildren()) {
            Path childPath = outputDir.resolve(sanitizeFileName(page.getTitle()));
            childernPages.add(convertPage(confluencePage, childPath));
        }
        preparedPage.setChildren(childernPages);


        return preparedPage;
    }
}
