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
        ConfluenceApiPage apiPage = apiInternalClient.getPageWithViewContentAndVersionById(contentId);

        ConfluenceContentModel res = new ConfluenceContentModel();

        var confluencePage = new ConfluencePage();
        confluencePage.setTitle(title);
        confluencePage.setType(ConfluenceContentModel.Type.VIEW);
        confluencePage.setContentFilePath(saveContent(apiPage));
        res.setPages(List.of(confluencePage));

        List<ConfluenceAttachment> list = apiInternalClient.getAttachments(contentId);
        Map<String, String> attachments = saveAttachments(list, outputDir);
        confluencePage.setAttachments(attachments);
     //todo   recursion
        return res;

    }

    private Map<String, String> saveAttachments(List<ConfluenceAttachment> list, Path outputDir) {
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
        apiInternalClient.saveUrlToFile(  attachment.getRelativeDownloadLink(), outputFilePath.toFile());
        return outputFilePath;
    }

    private String saveContent(ConfluenceApiPage apiPage) throws IOException {
        File file = outputDir.resolve(apiPage.getContentId() + ".xhtml").toFile();
        FileUtils.writeStringToFile(file, apiPage.getContent(), StandardCharsets.UTF_8);
        return file.toPath().toString();
    }


}
