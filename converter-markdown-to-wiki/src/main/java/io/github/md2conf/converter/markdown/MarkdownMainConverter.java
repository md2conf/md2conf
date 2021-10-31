package io.github.md2conf.converter.markdown;

import io.github.md2conf.converter.AbstractConverter;

public class MarkdownMainConverter extends AbstractConverter<MarkdownConverterConfigurationProperties> {

    public MarkdownMainConverter() {
        super(new MarkdownContentModelProducer());
    }

}
