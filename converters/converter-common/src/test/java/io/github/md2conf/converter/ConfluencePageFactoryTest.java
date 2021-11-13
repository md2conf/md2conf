package io.github.md2conf.converter;

import io.github.md2conf.model.ConfluencePage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

class ConfluencePageFactoryTest {

    private ConfluencePageFactory confluencePageFactory = new ConfluencePageFactory(ExtractTitleStrategy.FROM_FILENAME);

    @Test
    void pageByPath() {
        String path = "src/test/resources/just_text.wiki";
        File file = new File(path);
        ConfluencePage confluencePage  =confluencePageFactory.pageByPath(file.toPath());
        Assertions.assertThat(confluencePage).isNotNull();
        Assertions.assertThat(confluencePage.getTitle()).isNotNull();
        Assertions.assertThat(confluencePage.getContentFilePath()).contains(path);
    }
}