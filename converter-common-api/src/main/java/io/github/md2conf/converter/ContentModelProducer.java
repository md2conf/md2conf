package io.github.md2conf.converter;

import io.github.md2conf.model.ConfluenceContent;

public interface ContentModelProducer<P extends ConverterConfigurationProperties> {
    ConfluenceContent produce(P properties);

}
