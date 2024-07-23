package io.github.md2conf.converter.view2md;

import com.vladsch.flexmark.util.data.MutableDataSet;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.util.ModelFilesystemUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class View2MdConverterTest {

    @TempDir
    private Path outputPath;

    private View2MdConverter view2MdConverter;

    @BeforeEach
    void setUp() {
        view2MdConverter = new View2MdConverter(outputPath, new MutableDataSet());
    }

    @Test
    void convert_single_page_with_attachment() throws IOException {
        Path modelPath= Paths.get("src/test/resources/view_single_page/confluence-content-model.json");
        ConfluenceContentModel model = ModelFilesystemUtil.readModel(modelPath);
        PagesStructure pagesStructure = view2MdConverter.convert(model);
        Assertions.assertThat(pagesStructure).isNotNull();
        Assertions.assertThat(pagesStructure.pages()).hasSize(1);
        Assertions.assertThat(pagesStructure.pages().get(0).attachments()).hasSize(1);
        Assertions.assertThat(pagesStructure.pages().get(0).children()).isEmpty();
        assertThat(outputPath.resolve("Welcome to Confluence.md")).isRegularFile()
                .content().contains("# Welcome to Confluence");
    }

    @Test
    void convert_multiple() throws IOException {
        Path modelPath= Paths.get("src/test/resources/view_multiple_page/confluence-content-model.json");
        ConfluenceContentModel model = ModelFilesystemUtil.readModel(modelPath);
        PagesStructure pagesStructure = view2MdConverter.convert(model);
        Assertions.assertThat(pagesStructure).isNotNull();
        Assertions.assertThat(pagesStructure.pages()).hasSize(1);
        Assertions.assertThat(pagesStructure.pages().get(0).attachments()).hasSize(1);
        Assertions.assertThat(pagesStructure.pages().get(0).children()).hasSize(1);
        Assertions.assertThat(pagesStructure.pages().get(0).children().get(0).attachments()).isEmpty();
        Assertions.assertThat(pagesStructure.pages().get(0).children().get(0).children().get(0).attachments()).hasSize(3);
        Assertions.assertThat(pagesStructure.pages().get(0).children().get(0).children().get(0).children()).hasSize(0);
    }

    @Test
    void external_link_is_converted() {
        Path modelPath= Paths.get("src/test/resources/view_single_page/confluence-content-model.json");
        ConfluenceContentModel model = ModelFilesystemUtil.readModel(modelPath);
        PagesStructure pagesStructure = view2MdConverter.convert(model);
        Assertions.assertThat(pagesStructure).isNotNull();
        assertThat(outputPath.resolve("Welcome to Confluence.md")).isRegularFile()
                .content().contains("[Let's edit this page](/pages/viewpage.action?pageId=65549)")
                .contains("![welcome.png](Welcome%20to%20Confluence_attachments/welcome.png)");
    }

    @Test
    void crosspage_link_is_converted() {
        Path modelPath= Paths.get("src/test/resources/view_multiple_page/confluence-content-model.json");
        ConfluenceContentModel model = ModelFilesystemUtil.readModel(modelPath);
        PagesStructure pagesStructure = view2MdConverter.convert(model);
        Assertions.assertThat(pagesStructure).isNotNull();
        assertThat(outputPath.resolve("Welcome to Confluence.md")).isRegularFile()
                .content()
                .contains("[What is Confluence?](Welcome%20to%20Confluence/What%20is%20Confluence?%20(step%201%20of%209).md)")
                .contains("[A quick look at the editor](Welcome%20to%20Confluence/What%20is%20Confluence?%20(step%201%20of%209)/A%20quick%20look%20at%20the%20editor%20(step%202%20of%209).md)")
                .contains("![welcome.png](Welcome%20to%20Confluence_attachments/welcome.png)");
    }

    @Test
    void attachment_image_is_converted() {
        Path modelPath= Paths.get("src/test/resources/view_single_page/confluence-content-model.json");
        ConfluenceContentModel model = ModelFilesystemUtil.readModel(modelPath);
        PagesStructure pagesStructure = view2MdConverter.convert(model);
        Assertions.assertThat(pagesStructure).isNotNull();
        assertThat(outputPath.resolve("Welcome to Confluence.md")).isRegularFile()
                .content()
                .contains("![welcome.png](Welcome%20to%20Confluence_attachments/welcome.png)");
    }
}