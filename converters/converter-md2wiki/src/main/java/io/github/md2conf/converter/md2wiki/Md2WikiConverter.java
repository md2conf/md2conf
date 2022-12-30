package io.github.md2conf.converter.md2wiki;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.jira.converter.JiraConverterExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.IRender;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.github.md2conf.converter.AttachmentUtil;
import io.github.md2conf.converter.ConfluencePageFactory;
import io.github.md2conf.converter.Converter;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Md2WikiConverter implements Converter {

    static final DataHolder OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS, Arrays.asList(
                    TablesExtension.create(),
                    StrikethroughExtension.create(),
                    JiraConverterExtension.create()
            ));
    private final Parser parser;
    private final IRender renderer;

    private final ConfluencePageFactory confluencePageFactory;
    private final Path outputPath;

    public Md2WikiConverter(ConfluencePageFactory confluencePageFactory, Path outputPath) {
        this.confluencePageFactory = confluencePageFactory;
        this.outputPath = outputPath;
        this.parser = Parser.builder(OPTIONS).build();
        this.renderer = HtmlRenderer.builder(OPTIONS).build();
    }

    @Override
    public ConfluenceContentModel convert(PagesStructure pagesStructure) throws IOException {
        List<ConfluencePage> confluencePages = new ArrayList<>();
        for (Page topLevelPage : pagesStructure.pages()) { //use "for" loop to throw exception to caller
            ConfluencePage confluencePage;
            confluencePage = convertAndCreateConfluencePage(topLevelPage, Paths.get(""));
            confluencePages.add(confluencePage);
        }
        return new ConfluenceContentModel(confluencePages);
    }

    private ConfluencePage convertAndCreateConfluencePage(Page page, Path relativePart) throws IOException {

        String markdown = FileUtils.readFileToString(page.path().toFile(), Charset.defaultCharset()); //todo extract charset as parameter
        Node document = parser.parse(markdown);
        String wiki = renderer.render(document);

        //extract images and convert to local file paths if exists
        Set<String> imageUrls = ImageUrlCollector.collectImageUrls(document);
        List<Path> imagePaths = ImagePathUtil.filterExistingPaths(imageUrls, page.path().getParent());

        //calculate output file names
        String targetFileName = FilenameUtils.getBaseName(page.path().toString())+".wiki";
        Path targetPath = outputPath.resolve(relativePart).resolve(targetFileName);

        //copy converted content and attachments
        FileUtils.writeStringToFile(targetPath.toFile(), wiki, Charset.defaultCharset());
        List<Path> copiedAttachments = AttachmentUtil.copyPageAttachments(targetPath, page.attachments(), imagePaths);

        // create ConfluencePage model
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
                result.getChildren().add(convertAndCreateConfluencePage(childPage, outputPath.relativize(childrenDir)));
            }
        }
        //todo cross-links
        //todo title -postprocessors
        return result;
    }
}
