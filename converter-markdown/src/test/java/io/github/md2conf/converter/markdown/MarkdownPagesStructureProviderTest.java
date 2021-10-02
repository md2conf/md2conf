package io.github.md2conf.converter.markdown;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownPagesStructureProviderTest {


    @Test
    public void structure_nestedStructure_returnsMarkdownPagesStructureWithAllNonIncludeMdFiles() {
        // arrange
        Path documentationRootFolder = Paths.get("src/test/resources/markdown-based-pages-structure");
        MarkdownPagesStructureProvider fileBasedPagesStructureProvider = new MarkdownPagesStructureProvider(documentationRootFolder);

        // act
        MarkdownPagesStructure structure = fileBasedPagesStructureProvider.structure();

        // assert
        assertThat(structure.pages()).hasSize(1);

        MarkdownPage indexPage = markdownPageByPath(structure.pages(), documentationRootFolder.resolve("index.md"));
        assertThat(indexPage).isNotNull();
        assertThat(indexPage.children()).hasSize(2);

        MarkdownPage subPageOne = markdownPageByPath(indexPage.children(), documentationRootFolder.resolve("index/sub-page-one.md"));
        assertThat(subPageOne).isNotNull();
        assertThat(subPageOne.children()).hasSize(1);

        MarkdownPage subSubPageOne = markdownPageByPath(subPageOne.children(), documentationRootFolder.resolve("index/sub-page-one/sub-sub-page-one.md"));
        assertThat(subSubPageOne).isNotNull();
        assertThat(subSubPageOne.children()).isEmpty();

        MarkdownPage subPageTwo = markdownPageByPath(indexPage.children(), documentationRootFolder.resolve("index/sub-page-two.md"));
        assertThat(subPageTwo).isNotNull();
        assertThat(subPageTwo.children()).isEmpty();

        MarkdownPage excludePage = markdownPageByPath(indexPage.children(), documentationRootFolder.resolve("index/_exclude-page.md"));
        assertThat(excludePage).isNull();
    }


    private MarkdownPage markdownPageByPath(List<MarkdownPage> markdownPages, Path markdownPagePath) {
        return markdownPages.stream()
                            .filter((markdownPage) -> markdownPage.path().equals(markdownPagePath))
                            .findFirst()
                            .orElse(null);
    }

}