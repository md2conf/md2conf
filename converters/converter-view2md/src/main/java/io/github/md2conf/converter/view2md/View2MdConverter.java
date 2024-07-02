package io.github.md2conf.converter.view2md;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.html2md.converter.LinkConversion;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.github.md2conf.converter.ConfluenceModelConverter;
import io.github.md2conf.converter.view2md.internal.PreparedPage;
import io.github.md2conf.converter.view2md.internal.PreparedPageFactory;
import io.github.md2conf.converter.view2md.internal.PreparedPageStructure;
import io.github.md2conf.indexer.DefaultPage;
import io.github.md2conf.indexer.DefaultPagesStructure;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.markdown.formatter.MarkdownFormatter;
import io.github.md2conf.model.ConfluenceContentModel;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            .set(EXT_INLINE_LINK, LinkConversion.MARKDOWN_EXPLICIT );

    public View2MdConverter(Path outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public PagesStructure convert(ConfluenceContentModel model)  {

        PreparedPageStructure preparedPageStructure = PreparedPageFactory.fromModel(model, outputDir);
        Map<Long,Path> pageIdPathMap = structureAsMap(preparedPageStructure);
        List<DefaultPage> resList = new ArrayList<>();
        for (PreparedPage page : preparedPageStructure.getPages()) {
            try {
                resList.add(convertPage(page, pageIdPathMap, outputDir));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new DefaultPagesStructure(resList);
    }

    private DefaultPage convertPage(PreparedPage page, Map<Long, Path> pageIdPathMap, Path outputDir) throws IOException {
        String html = FileUtils.readFileToString(page.getSourcePath().toFile(), Charset.defaultCharset());
        String md = FlexmarkHtmlConverter.builder(options).build().convert(html);
        md = "#" + page.getPageTitle() +"\n\n" + md;
        List<Path> attachments = copyAttachmentsMap(page.getTargetPath(), page.getAttachments());
        String formattedText = MarkdownFormatter.format(md, attachments, pageIdPathMap, outputDir);
        FileUtils.writeStringToFile(page.getTargetPath().toFile(), formattedText, Charset.defaultCharset());
        List<DefaultPage> childrenPages = new ArrayList<>();
        for (PreparedPage child: page.getChildren()){
            childrenPages.add(convertPage(child, pageIdPathMap, outputDir.resolve(page.getPageTitle())));
        }
        return new DefaultPage(page.getTargetPath(), childrenPages, attachments);
    }

    private static Map<Long,Path> structureAsMap(PreparedPageStructure preparedPageStructure){
        Map<Long,Path> pageIdToPathMap = new HashMap<>();
        for (PreparedPage page: preparedPageStructure.getPages()){
            addPageToMap(pageIdToPathMap, page);
        }
        return pageIdToPathMap;
    }

    private static void addPageToMap(Map<Long,Path> map, PreparedPage preparedPage){
        map.put(preparedPage.getPageId(),preparedPage.getTargetPath());
        for (PreparedPage child: preparedPage.getChildren()){
            addPageToMap(map, child);
        }
    }

}
