package io.github.md2conf.converter;

import io.github.md2conf.model.ConfluenceContentModel;

public interface ContentModelProducer<P extends ConverterConfigurationProperties> {
    ConfluenceContentModel produce(P properties);

}
