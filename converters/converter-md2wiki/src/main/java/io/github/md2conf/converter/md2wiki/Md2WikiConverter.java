package io.github.md2conf.converter.md2wiki;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.jira.converter.JiraConverterExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.github.md2conf.converter.AttachmentUtil;
import io.github.md2conf.converter.ConfluencePageFactory;
import io.github.md2conf.converter.Converter;
import io.github.md2conf.converter.md2wiki.attachment.ImageFilePathUtil;
import io.github.md2conf.converter.md2wiki.attachment.ImageUrlUtil;
import io.github.md2conf.flexmart.ext.crosspage.links.CrosspageLinkExtension;
import io.github.md2conf.flexmart.ext.local.attachments.LocalAttachmentLinkExtension;
import io.github.md2conf.indexer.Page;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static io.github.md2conf.converter.md2wiki.attachment.LocalAttachmentUtil.collectLocalAttachmentPaths;

public class Md2WikiConverter implements Converter {

    public static final DataHolder OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS, Arrays.asList(
                    TablesExtension.create(),
                    StrikethroughExtension.create(),
                    LocalAttachmentLinkExtension.create(),
                    CrosspageLinkExtension.create(),
                    JiraConverterExtension.create()
            ));

    private final ConfluencePageFactory confluencePageFactory;
    private final Path outputPath;

    public Md2WikiConverter(ConfluencePageFactory confluencePageFactory, Path outputPath) {
        this.confluencePageFactory = confluencePageFactory;
        this.outputPath = outputPath;
    }

    @Override
    public ConfluenceContentModel convert(PagesStructure pagesStructure) throws IOException {
        List<ConfluencePage> confluencePages = new ArrayList<>();
        for (Page topLevelPage : pagesStructure.pages()) { //use "for" loop to throw exception to caller
            ConfluencePage confluencePage;
            confluencePage = convertAndCreateConfluencePage(topLevelPage, Paths.get(""), pagesStructure);
            confluencePages.add(confluencePage);
        }
        return new ConfluenceContentModel(confluencePages);
    }

    /**
     * @param page           - a Page
     * @param relativePart   - relative path to target path, used to process children recursively
     * @param pagesStructure - page structure
     * @return ConfluencePage
     */
    private ConfluencePage convertAndCreateConfluencePage(Page page, Path relativePart, PagesStructure pagesStructure) throws IOException {

        //read markdown file from Page path
        String markdown = FileUtils.readFileToString(page.path().toFile(), Charset.defaultCharset()); //todo extract charset as parameter

        //Convert to wiki using FlexMark parser and renderer
        DataHolder flexmarkOptions = OPTIONS.toMutable()
                .set(LocalAttachmentLinkExtension.CURRENT_FILE_PATH, page.path().getParent())
                .set(CrosspageLinkExtension.CURRENT_FILE_PATH, page.path().getParent())
                .set(CrosspageLinkExtension.PAGES_STRUCTURE, pagesStructure)
                .set(CrosspageLinkExtension.EXTRACT_TITLE_STRATEGY, confluencePageFactory.getExtractTitleStrategy())
                .toImmutable();
        Parser parser = Parser.builder(flexmarkOptions).build();
        HtmlRenderer renderer = HtmlRenderer.builder(flexmarkOptions).build();
        Node document = parser.parse(markdown);
        String wiki = renderer.render(document);

        //extract images Urls and convert to local file paths if exists
        List<Path> imagePaths = extractLocalImagePaths(page, document);
        List<Path> localAttachmentPaths = collectLocalAttachmentPaths(document);

        //calculate output file names
        String targetFileName = FilenameUtils.getBaseName(page.path().toString())+".wiki";
        Path targetPath = outputPath.resolve(relativePart).resolve(targetFileName);

        //copy converted content and attachments
        FileUtils.writeStringToFile(targetPath.toFile(), wiki, Charset.defaultCharset());
        List<Path> copiedAttachments = AttachmentUtil.copyPageAttachments(targetPath, page.attachments(), imagePaths, localAttachmentPaths);

        // create ConfluencePage model
        ConfluencePage result = confluencePageFactory.pageByPath(targetPath);
        result.setType(ConfluenceContentModel.Type.WIKI);
        result.setAttachments(AttachmentUtil.toAttachmentsMap(copiedAttachments));
        // process children
        if (page.children() != null && !page.children().isEmpty()) {
            String childrenDirAsStr = FilenameUtils.concat(
                    relativePart.toString(),
                    FilenameUtils.removeExtension(targetPath.getFileName().toString()));
            Path childrenDir = outputPath.resolve(childrenDirAsStr);
            if (!childrenDir.toFile().mkdirs()) {
                throw new IOException("Cannot create dirs in " + childrenDir);
            }
            for (Page childPage : page.children()) {
                result.getChildren().add(convertAndCreateConfluencePage(childPage, outputPath.relativize(childrenDir), pagesStructure));
            }
        }
        return result;
    }

    private static List<Path> extractLocalImagePaths(Page page, Node document) {
        Set<String> imageUrls = ImageUrlUtil.collectUrlsOfImages(document);
        return ImageFilePathUtil.filterExistingPaths(imageUrls, page.path().getParent());
    }
}
