package io.github.md2conf.converter;

import io.github.md2conf.model.ConfluenceContentModel;

import java.nio.file.Path;

import static io.github.md2conf.model.util.ModelReadWriteUtil.saveConfluenceContentModelToFilesystem;

public abstract class AbstractConverter<P extends ConverterConfigurationProperties>{

    private final ContentModelProducer<P> contentModelConverter;

    public AbstractConverter(ContentModelProducer<P> contentModelConverter) {
        this.contentModelConverter = contentModelConverter;
    }

    public void convertAndSave(P p){
        ConfluenceContentModel content = contentModelConverter.produce(p);
        Path outputPath = Path.of(p.getOutputDirectory());
        saveConfluenceContentModelToFilesystem(content, outputPath);
    }


}
