package io.github.md2conf.converter.markdown;

import io.github.md2conf.converter.ContentModelProducer;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class MarkdownContentModelProducer implements ContentModelProducer<MarkdownConverterConfigurationProperties> {

    public ConfluenceContentModel produce(MarkdownConverterConfigurationProperties properties) {

        Path path = Path.of(properties.getInputDirectory());
        MarkdownPagesStructureProvider structureProvider = new MarkdownPagesStructureProvider(path);
        List<ConfluencePage> confluencePages = structureProvider
                .structure()
                .pages()
                .stream()
                .map(this::convertPage)
                .collect(Collectors.toList());
        return new ConfluenceContentModel(confluencePages);
    }

    private ConfluencePage convertPage(MarkdownPage markdownPage) {
        ConfluencePage confluencePage = new ConfluencePage();
        confluencePage.setTitle(title(markdownPage));
        confluencePage.setContentFilePath(markdownPage.path().toString()); //todo copy new content


        confluencePage.setType(ConfluenceContentModel.Type.WIKI);// todo
        List<ConfluencePage> children = markdownPage.children().stream().map(this::convertPage).collect(Collectors.toList());
        confluencePage.setChildren(children);
        return confluencePage;
    }

    private static String title(MarkdownPage markdownPage) {
        return markdownPage.path().getFileName().toString();
    }

}
