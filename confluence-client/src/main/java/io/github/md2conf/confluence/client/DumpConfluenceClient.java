package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.http.ApiInternalClient;
import io.github.md2conf.confluence.client.http.ConfluenceAttachment;
import io.github.md2conf.confluence.client.http.ConfluencePage;
import io.github.md2conf.model.ConfluenceContentModel;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class DumpConfluenceClient {

    private final ApiInternalClient apiInternalClient;
    private final Path outputDir;

    public DumpConfluenceClient(ApiInternalClient apiInternalClient, Path outputDir) {
        this.apiInternalClient = apiInternalClient;
        this.outputDir = outputDir;
    }

    public ConfluenceContentModel dump(String spaceKey, String title) throws IOException {
        String contentId = apiInternalClient.getPageByTitle(spaceKey, title);
        ConfluencePage page = apiInternalClient.getPageWithContentAndVersionById(contentId);

        ConfluenceContentModel res = new ConfluenceContentModel();
        var confluencePage = new io.github.md2conf.model.ConfluencePage();
        confluencePage.setTitle(title);
        confluencePage.setType(ConfluenceContentModel.Type.VIEW);
        confluencePage.setContentFilePath(saveContent(page));
        res.setPages(List.of(confluencePage));

        List<ConfluenceAttachment> list = apiInternalClient.getAttachments(contentId);
        return res;

    }

    private String saveContent(ConfluencePage page) throws IOException {
        File file = new File(outputDir.toFile(), page.getContentId());
        FileUtils.writeStringToFile(file, page.getContent(), StandardCharsets.UTF_8);
        return file.toPath().toString();
    }


}
