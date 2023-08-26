package io.github.md2conf.title.processor;

import io.github.md2conf.indexer.DefaultPage;
import io.github.md2conf.indexer.DefaultPagesStructure;
import io.github.md2conf.indexer.PagesStructure;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

class DefaultPageStructureTitleProcessorTest {

    DefaultPageStructureTitleProcessor defaultPageStructureTitleProcessor = new DefaultPageStructureTitleProcessor(TitleExtractStrategy.FROM_FILENAME, null, null, false);


    @Test
    void empty_page_structure() throws IOException {
        Map<Path, String> map = defaultPageStructureTitleProcessor.toTitleMap(new DefaultPagesStructure(List.of()));
        Assertions.assertThat(map).isEmpty();
    }


    @Test
    void one_page_structure() throws IOException {
        Path path = Path.of("tmp/1.md").toAbsolutePath();
        Map<Path, String> map = defaultPageStructureTitleProcessor.toTitleMap(
                new DefaultPagesStructure(List.of(new DefaultPage(path)))
        );
        Assertions.assertThat(map).hasSize(1);
        Assertions.assertThat(map.get(path)).isEqualTo("1");
    }

    @Test
    void two_page_structure() throws IOException {
        Path path1 = Path.of("tmp/1.md").toAbsolutePath();
        Path path2 = Path.of("tmp/2.md").toAbsolutePath();
        Map<Path, String> map = defaultPageStructureTitleProcessor.toTitleMap(
                new DefaultPagesStructure(List.of(new DefaultPage(path1), new DefaultPage(path2)))
        );
        Assertions.assertThat(map).hasSize(2);
        Assertions.assertThat(map.get(path1)).isEqualTo("1");
        Assertions.assertThat(map.get(path2)).isEqualTo("2");
    }

    @Test
    void tree_page_structure() throws IOException {
        Path path_lvl0 = Path.of("tmp/1.md").toAbsolutePath();
        Path path_lvl1_0 = Path.of("tmp/1/path_lvl1_0.md").toAbsolutePath();
        Path path_lvl1_1 = Path.of("tmp/1/path_lvl1_1.md").toAbsolutePath();
        Path path_lvl2 = Path.of("tmp/1/2/path_lvl2.md").toAbsolutePath();
        PagesStructure ps = createPagesStructure(path_lvl0, path_lvl1_0, path_lvl1_1, path_lvl2);

        Map<Path, String> map = defaultPageStructureTitleProcessor.toTitleMap(ps);
        Assertions.assertThat(map).hasSize(4);
        Assertions.assertThat(map.get(path_lvl0)).isEqualTo("1");
        Assertions.assertThat(map.get(path_lvl1_0)).isEqualTo("path_lvl1_0");
        Assertions.assertThat(map.get(path_lvl1_1)).isEqualTo("path_lvl1_1");
        Assertions.assertThat(map.get(path_lvl2)).isEqualTo("path_lvl2");
    }

    @Test
    void tree_page_structure_prefixed_suffixed() throws IOException {
        Path path_lvl0 = Path.of("tmp/1.md").toAbsolutePath();
        Path path_lvl1_0 = Path.of("tmp/1/path_lvl1_0.md").toAbsolutePath();
        Path path_lvl1_1 = Path.of("tmp/1/path_lvl1_1.md").toAbsolutePath();
        Path path_lvl2 = Path.of("tmp/1/2/path_lvl2.md").toAbsolutePath();
        PagesStructure ps = createPagesStructure(path_lvl0, path_lvl1_0, path_lvl1_1, path_lvl2);

        DefaultPageStructureTitleProcessor titleProcessor = new DefaultPageStructureTitleProcessor(TitleExtractStrategy.FROM_FILENAME, "Pre", "end", false);

        Map<Path, String> map = titleProcessor.toTitleMap(ps);
        Assertions.assertThat(map).hasSize(4);
        Assertions.assertThat(map.get(path_lvl0)).isEqualTo("Pre - 1 - end");
        Assertions.assertThat(map.get(path_lvl1_0)).isEqualTo("Pre - path_lvl1_0 - end");
        Assertions.assertThat(map.get(path_lvl1_1)).isEqualTo("Pre - path_lvl1_1 - end");
        Assertions.assertThat(map.get(path_lvl2)).isEqualTo("Pre - path_lvl2 - end");
    }

    @Test
    void tree_page_structure_title_child_prefixed() throws IOException {
        Path path_lvl0 = Path.of("tmp/ROOT.md").toAbsolutePath();
        Path path_lvl1_0 = Path.of("tmp/1/Child-01.md").toAbsolutePath();
        Path path_lvl1_1 = Path.of("tmp/1/Child-02.md").toAbsolutePath();
        Path path_lvl2 = Path.of("tmp/1/2/Child-01-01.md").toAbsolutePath();
        PagesStructure ps = createPagesStructure(path_lvl0, path_lvl1_0, path_lvl1_1, path_lvl2);

        DefaultPageStructureTitleProcessor titleProcessor = new DefaultPageStructureTitleProcessor(TitleExtractStrategy.FROM_FILENAME, "Pre", "end", true);

        Map<Path, String> map = titleProcessor.toTitleMap(ps);
        Assertions.assertThat(map).hasSize(4);
        Assertions.assertThat(map.get(path_lvl0)).isEqualTo("ROOT");
        Assertions.assertThat(map.get(path_lvl1_0)).isEqualTo("ROOT - Child-01");
        Assertions.assertThat(map.get(path_lvl1_1)).isEqualTo("ROOT - Child-02");
        Assertions.assertThat(map.get(path_lvl2)).isEqualTo("ROOT - Child-01-01");
    }

    @NotNull
    private static PagesStructure createPagesStructure(Path path_lvl0, Path path_lvl1_0, Path path_lvl1_1, Path path_lvl2) {
        DefaultPage page_lvl0 = new DefaultPage(path_lvl0);
        DefaultPage page_lvl1_0 = new DefaultPage(path_lvl1_0);
        DefaultPage page_lvl1_1 = new DefaultPage(path_lvl1_1);
        DefaultPage page_lvl2 = new DefaultPage(path_lvl2);
        page_lvl0.addChild(page_lvl1_0);
        page_lvl0.addChild(page_lvl1_1);
        page_lvl1_0.addChild(page_lvl2);
        return new DefaultPagesStructure(List.of(page_lvl0));
    }
}