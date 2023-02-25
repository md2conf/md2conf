package io.github.md2conf.converter.noop;

import io.github.md2conf.converter.Converter;
import io.github.md2conf.indexer.DefaultFileIndexer;
import io.github.md2conf.indexer.FileIndexer;
import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.title.processor.DefaultPageStructureTitleProcessor;
import io.github.md2conf.title.processor.PageStructureTitleProcessor;
import io.github.md2conf.title.processor.TitleExtractStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

class NoopConverterTest {

    static PageStructureTitleProcessor pageStructureTitleProcessor = new DefaultPageStructureTitleProcessor(TitleExtractStrategy.FROM_FILENAME, null, null, false);
    FileIndexer fileIndexer = new DefaultFileIndexer(new FileIndexerConfigurationProperties());

    public static Stream<Arguments> converters() {
        return Stream.of(
                arguments(new NoopConverter(pageStructureTitleProcessor, Boolean.FALSE )),
                arguments(new NoopConverter(pageStructureTitleProcessor, Boolean.TRUE ))
        );
    }

    @ParameterizedTest
    @MethodSource("converters")
    void convert_dir_with_attachments(Converter converter) throws IOException {
        String path = "src/test/resources/dir_with_attachments";
        File f = new File(path);
        PagesStructure structure = fileIndexer.indexPath(f.toPath());
        Assertions.assertThat(structure.pages()).hasSize(1);
        ConfluenceContentModel model = converter.convert(structure);
        Assertions.assertThat(model).isNotNull();
        Assertions.assertThat(model.getPages().get(0)).isNotNull();
        Assertions.assertThat(model.getPages().get(0).getAttachments()).hasSize(2);
        Assertions.assertThat(model.getPages().get(0).getAttachments().keySet()).contains("1.txt");
        Assertions.assertThat(model.getPages().get(0).getAttachments().keySet()).contains("attach.wiki");
        Assertions.assertThat(model.getPages().get(0).getChildren()).hasSize(1);
    }


}