package io.github.md2conf.converter.view2md;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.html2md.converter.LinkConversion;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.github.md2conf.converter.ConfluenceModelConverter;
import io.github.md2conf.indexer.DefaultPage;
import io.github.md2conf.indexer.DefaultPagesStructure;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.markdown.formatter.MarkdownFormatter;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter.BR_AS_EXTRA_BLANK_LINES;
import static com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter.BR_AS_PARA_BREAKS;
import static com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter.EXTRACT_AUTO_LINKS;
import static com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter.EXT_INLINE_LINK;
import static com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter.LIST_CONTENT_INDENT;
import static com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter.OUTPUT_ATTRIBUTES_ID;
import static com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter.SKIP_ATTRIBUTES;
import static com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter.WRAP_AUTO_LINKS;
import static io.github.md2conf.converter.AttachmentUtil.copyAttachmentsMap;
import static io.github.md2conf.converter.view2md.ConfluenceContentModelUtil.pageIdToPathMap;
import static io.github.md2conf.converter.view2md.FileNameUtil.getTargetPath;

public class View2MdConverter implements ConfluenceModelConverter {

    private final Path outputDir;
    private final MutableDataSet options = new MutableDataSet()
            .set(LIST_CONTENT_INDENT, false)
            .set(SKIP_ATTRIBUTES, true)
            .set(EXTRACT_AUTO_LINKS, false)
            .set(WRAP_AUTO_LINKS, false)
            .set(OUTPUT_ATTRIBUTES_ID, false)
            .set(LIST_CONTENT_INDENT, false)
            .set(BR_AS_EXTRA_BLANK_LINES, false)
            .set(BR_AS_PARA_BREAKS, false)
            .set(EXT_INLINE_LINK, LinkConversion.MARKDOWN_EXPLICIT );

    public View2MdConverter(Path outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public PagesStructure convert(ConfluenceContentModel model)  {
        List<ConfluencePage> pageList = model.getPages();
        List<DefaultPage> resList = new ArrayList<>();
        for (ConfluencePage page : pageList) {
            try {
                resList.add(convertPage(page, model, outputDir));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new DefaultPagesStructure(resList);
    }

    private DefaultPage convertPage(ConfluencePage page, ConfluenceContentModel model, Path outputDir) throws IOException {
        Path targetPath = getTargetPath(page, outputDir);
        String html = FileUtils.readFileToString(new File(page.getContentFilePath()), Charset.defaultCharset());
        String md = FlexmarkHtmlConverter.builder(options).build().convert(html);
        md = "#" + page.getTitle() +"\n\n" + md;
        List<Path> attachments = copyAttachmentsMap(targetPath, page.getAttachments());
        String formattedText = MarkdownFormatter.format(md, attachments, pageIdToPathMap(model, outputDir));
        FileUtils.writeStringToFile(targetPath.toFile(), formattedText, Charset.defaultCharset());
        List<DefaultPage> childrenPages = new ArrayList<>();
        for (ConfluencePage child: page.getChildren()){
            childrenPages.add(convertPage(child, model, outputDir.resolve(page.getTitle())));
        }
        return new DefaultPage(targetPath, childrenPages, attachments);
    }

}
