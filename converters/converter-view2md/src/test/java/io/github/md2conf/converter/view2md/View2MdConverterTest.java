package io.github.md2conf.converter.view2md;

import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.util.ModelReadWriteUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class View2MdConverterTest {

    @TempDir
    private Path outputPath;

    private View2MdConverter view2MdConverter;

    @BeforeEach
    void setUp() {
        view2MdConverter = new View2MdConverter(outputPath);
    }

    @Test
    void convert_single_page_with_attachment() throws IOException {
        Path modelPath= Paths.get("src/test/resources/view_single_page/confluence-content-model.json");
        ConfluenceContentModel model = ModelReadWriteUtil.readFromYamlOrJson(modelPath.toFile());
        PagesStructure pagesStructure = view2MdConverter.convert(model);
        Assertions.assertThat(pagesStructure).isNotNull();
        Assertions.assertThat(pagesStructure.pages()).hasSize(1);
        Assertions.assertThat(pagesStructure.pages().get(0).attachments()).hasSize(1);
        Assertions.assertThat(pagesStructure.pages().get(0).children()).isEmpty();
    }

    @Test
    void convert_multiple() throws IOException {
        Path modelPath= Paths.get("src/test/resources/view_multiple_page/confluence-content-model.json");
        ConfluenceContentModel model = ModelReadWriteUtil.readFromYamlOrJson(modelPath.toFile());
        PagesStructure pagesStructure = view2MdConverter.convert(model);
        Assertions.assertThat(pagesStructure).isNotNull();
        Assertions.assertThat(pagesStructure.pages()).hasSize(1);
        Assertions.assertThat(pagesStructure.pages().get(0).attachments()).hasSize(1);
        Assertions.assertThat(pagesStructure.pages().get(0).children()).hasSize(1);
        Assertions.assertThat(pagesStructure.pages().get(0).children().get(0).attachments()).isEmpty();
        Assertions.assertThat(pagesStructure.pages().get(0).children().get(0).children().get(0).attachments()).hasSize(3);
        Assertions.assertThat(pagesStructure.pages().get(0).children().get(0).children().get(0).children()).hasSize(0);
    }
}