package io.github.md2conf.converter;

import io.github.md2conf.model.ConfluenceContent;

import java.nio.file.Path;

import static io.github.md2conf.model.util.ReadWriteUtil.saveConfluenceContentModelToFilesystem;

public abstract class MainConverter<P extends ConverterConfigurationProperties>{

    private final ContentModelProducer<P> contentModelConverter;

    public MainConverter(ContentModelProducer<P> contentModelConverter) {
        this.contentModelConverter = contentModelConverter;
    }

    public void convertAndSave(P p){
        ConfluenceContent content = contentModelConverter.produce(p);
        Path outputPath = Path.of(p.getOutputDirectory());
        saveConfluenceContentModelToFilesystem(content, outputPath);
    }


}
