package io.github.md2conf.converter.view2md;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.html2md.converter.LinkConversion;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.github.md2conf.converter.ConfluenceModelConverter;
import io.github.md2conf.indexer.DefaultPage;
import io.github.md2conf.indexer.DefaultPagesStructure;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

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
            .set(EXT_INLINE_LINK, LinkConversion.TEXT ); //todo enable link conversion

    public View2MdConverter(Path outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public PagesStructure convert(ConfluenceContentModel model)  {
        List<ConfluencePage> pageList = model.getPages();
        List<DefaultPage> resList = new ArrayList<>();
        for (ConfluencePage page : pageList) {
            try {
                resList.add(convertPage(page, outputDir));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new DefaultPagesStructure(resList);
    }

    private DefaultPage convertPage(ConfluencePage page, Path outputDir) throws IOException {
        var baseName = FilenameUtils.getBaseName(page.getContentFilePath());
        var resName = baseName+".md";
        Path targetPath = outputDir.resolve(resName);
        String html = FileUtils.readFileToString(new File(page.getContentFilePath()), Charset.defaultCharset());
        String md = FlexmarkHtmlConverter.builder(options).build().convert(html);
        FileUtils.writeStringToFile(targetPath.toFile(), md, Charset.defaultCharset());
        List<Path> attachments = copyAttachmentsMap(targetPath, page.getAttachments());
        List<DefaultPage> childrenPages = new ArrayList<>();
        for (ConfluencePage child: page.getChildren()){
            childrenPages.add(convertPage(child, outputDir.resolve(baseName)));
        }
        return new DefaultPage(targetPath, childrenPages, attachments);
    }

}
