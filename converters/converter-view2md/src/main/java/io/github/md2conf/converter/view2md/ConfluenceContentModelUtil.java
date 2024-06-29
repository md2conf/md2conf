package io.github.md2conf.converter.view2md;

import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static io.github.md2conf.converter.view2md.FileNameUtil.getTargetPath;

public class ConfluenceContentModelUtil {

    private static final Logger log = LoggerFactory.getLogger(ConfluenceContentModelUtil.class);

    /**
     * Create Map of  pageId to pagePath, relative to outputDir.
     *
     * @param model - input model
     * @param outputDir - outputDir
     * @return Map of  pageId to pagePath, relative to outputDir.
     */
    public static Map<Long, Path> pageIdToPathMap(ConfluenceContentModel model, Path outputDir){
        Map<Long, Path> map = new HashMap<>();
        for (ConfluencePage confluencePage : model.getPages()){
            addPageToMap(map, confluencePage, outputDir, outputDir);
        }
        return map;
    }

    private static void addPageToMap(Map<Long, Path> map, ConfluencePage confluencePage, Path baseOutput, Path currentOutput) {
        Long pageId = extractPageId(confluencePage.getContentFilePath());
        Path targetPath = getTargetPath(confluencePage, currentOutput);
        map.put(pageId, baseOutput.relativize(targetPath)); //todo fix
        for (ConfluencePage page: confluencePage.getChildren()){
            addPageToMap(map, page, baseOutput, currentOutput.resolve(page.getTitle()));
        }
    }

    private static Long extractPageId(String contentFilePath) {
        try {
            return Long.parseLong(FilenameUtils.getBaseName(contentFilePath));
        }catch (NumberFormatException e){
            log.warn("Cannot extract page id from content file {}", contentFilePath);
            return null;
        }
    }
}
