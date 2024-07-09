package io.github.md2conf.markdown.formatter;

import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension;
import com.vladsch.flexmark.ext.ins.InsExtension;
import com.vladsch.flexmark.ext.superscript.SuperscriptExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKeyBase;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vladsch.flexmark.util.format.options.HeadingStyle.AS_IS;

public class MarkdownFormatter {

    final MutableDataSet formatOptions;
    final Parser parser;
    final Formatter renderer;

    public MarkdownFormatter() {
        this(new HashMap<>());
    }

    public MarkdownFormatter(DataHolder dataHolder) {
        this(dataHolder.getAll());
    }

    private MarkdownFormatter(Map<? extends DataKeyBase<?>, Object> userOptions) {
        DataHolder defaultOptions = ParserEmulationProfile.GITHUB_DOC.getProfileOptions()
                .set(Formatter.RIGHT_MARGIN, 120)
                .set(Formatter.HEADING_STYLE, AS_IS)
                .set(Formatter.MAX_TRAILING_BLANK_LINES, 1)
                .set(Formatter.KEEP_IMAGE_LINKS_AT_START, Boolean.TRUE)
//            .set(Formatter.KEEP_SOFT_LINE_BREAKS, Boolean.TRUE)
//            .set(Formatter.KEEP_HARD_LINE_BREAKS, Boolean.TRUE)
                .set(Parser.EXTENSIONS, Arrays.asList(
//                    DefinitionExtension.create(),
                        EmojiExtension.create(),
//                    FootnoteExtension.create(),
                        StrikethroughSubscriptExtension.create(),
                        InsExtension.create(),
                        SuperscriptExtension.create(),
                        TablesExtension.create(),
//                    TocExtension.create(),
//                    SimTocExtension.create(),
                        WikiLinkExtension.create()
                ));
        // merge default options with user options

        //noinspection unchecked
        userOptions.keySet().stream()
                .filter(key -> userOptions.get(key) != null)
                .forEach(key -> ((Map<DataKeyBase<?>, Object>) defaultOptions.getAll()).put(key, userOptions.get(key)));
        parser = Parser.builder(defaultOptions).build();

        formatOptions = new MutableDataSet();
        formatOptions.set(Parser.EXTENSIONS, Parser.EXTENSIONS.get(defaultOptions));
        renderer = Formatter.builder(formatOptions).build();
    }

    public String format(String text) {
        return format(text, List.of(), Map.of(), Path.of("."));
    }

    public String format(String text, List<Path> attachments, Map<Long, Path> pageIdPathMap, Path currentDir) {
        Node document = parser.parse(text);
        ImageAttachmentUrlReplacer visitor = new ImageAttachmentUrlReplacer(attachments);
        visitor.replaceUrl(document);
        CrosspageLinkReplacer crosspageLinkReplacer = new CrosspageLinkReplacer(pageIdPathMap, currentDir);
        crosspageLinkReplacer.replacePageLinks(document);
        return renderer.render(document);
    }

    protected Parser getParser() {
        return parser;
    }

    public Formatter getRenderer() {
        return renderer;
    }
}
