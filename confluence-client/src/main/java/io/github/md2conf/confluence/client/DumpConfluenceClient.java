package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.http.ApiInternalClient;
import io.github.md2conf.confluence.client.http.ConfluenceApiPage;
import io.github.md2conf.confluence.client.http.ConfluenceAttachment;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DumpConfluenceClient {

    private final ApiInternalClient apiInternalClient;
    private final Path outputDir;

    public DumpConfluenceClient(ApiInternalClient apiInternalClient, Path outputDir) {
        this.apiInternalClient = apiInternalClient;
        this.outputDir = outputDir;
    }

    public ConfluenceContentModel dump(String spaceKey, String title) throws IOException {
        String contentId = apiInternalClient.getPageByTitle(spaceKey, title);
        ConfluenceApiPage apiPage = apiInternalClient.getPageWithViewContent(contentId);
        //process top-level page
        ConfluencePage topLevelPage = processAndSave(apiPage, outputDir);

        List<ConfluenceApiPage> childrenPages = apiInternalClient.getChildPagesWithViewContent(contentId);
        List<ConfluencePage> confluencePages = new ArrayList<>();
        for (ConfluenceApiPage child: childrenPages){
            ConfluencePage childConfluencePage = processAndSave(child, outputDir.resolve(contentId));
            confluencePages.add(childConfluencePage);
        }
        topLevelPage.setChildren(confluencePages);
        ConfluenceContentModel res = new ConfluenceContentModel();
        res.setPages(List.of(topLevelPage));
        return res;

    }

    private ConfluencePage processAndSave(ConfluenceApiPage apiPage,  Path outputDir) throws IOException {
        var confluencePage = new ConfluencePage();
        confluencePage.setTitle(apiPage.getTitle());
        confluencePage.setType(ConfluenceContentModel.Type.VIEW);
        confluencePage.setContentFilePath(saveContent(apiPage, outputDir));

        List<ConfluenceAttachment> list = apiInternalClient.getAttachments(apiPage.getContentId());
        Map<String, String> attachments = saveAttachments(list, outputDir);
        confluencePage.setAttachments(attachments);
        return confluencePage;
    }

    private  Map<String, String> saveAttachments(List<ConfluenceAttachment> list, Path outputDir) {
        Map<String,String> res = new HashMap<>();
        for (ConfluenceAttachment attachment: list){
            Path outputPath =  saveAttachment(attachment, outputDir);
             res.put(attachment.getTitle(), outputPath.toString());
         }
        return res;
    }

    /**
     * save attachment and return path to savedFile
     * @param attachment
     * @param outputDir
     * @return path to savedFile
     */
    private Path saveAttachment(ConfluenceAttachment attachment, Path outputDir) {
        Path outputFilePath = outputDir.resolve(attachment.getTitle());
        //todo if file exists - warn
        apiInternalClient.saveUrlToFile(attachment.getRelativeDownloadLink(), outputFilePath.toFile());
        return outputFilePath;
    }

    private static String saveContent(ConfluenceApiPage apiPage, Path outputDir) throws IOException {
        File file = outputDir.resolve(apiPage.getContentId() + ".xhtml").toFile();
        FileUtils.writeStringToFile(file, apiPage.getContent(), StandardCharsets.UTF_8);
        return file.toPath().toString();
    }


}
