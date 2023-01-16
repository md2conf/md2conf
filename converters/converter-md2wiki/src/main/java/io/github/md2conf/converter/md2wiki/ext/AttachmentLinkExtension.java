package io.github.md2conf.converter.md2wiki.ext;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataHolder;

public class AttachmentLinkExtension implements Parser.ParserExtension{

    private AttachmentLinkExtension() { }

    public static AttachmentLinkExtension create() {
        return new AttachmentLinkExtension();
    }

    @Override
    public void parserOptions(MutableDataHolder options) {
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessorFactory(new AttachmentLinkPostProcessor.Factory(parserBuilder));
    }
}
