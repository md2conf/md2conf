package io.github.md2conf.flexmart.ext.crosspage.links;

import com.vladsch.flexmark.core.test.util.RendererSpecTest;
import com.vladsch.flexmark.jira.converter.JiraConverterExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.github.md2conf.converter.ExtractTitleStrategy;
import io.github.md2conf.indexer.DefaultFileIndexer;
import io.github.md2conf.indexer.PagesStructure;
import org.jetbrains.annotations.NotNull;
import org.junit.runners.Parameterized;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static io.github.md2conf.flexmart.ext.crosspage.links.CrosspageLinkExtension.*;

public class CrosspageLinkTitleFromFilenameSpecTest extends RendererSpecTest {
    final private static String SPEC_RESOURCE = "/crosspage_link_title_from_filename_spec_test.md";
    final public static @NotNull ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(SPEC_RESOURCE);

    final private static Path sample_page_path = new File("src/test/resources/sample_page.md").toPath();
    final private static PagesStructure TEST_PAGES_STRUCTURE = new DefaultFileIndexer.DefaultPagesStructure(
            List.of(new DefaultFileIndexer.DefaultPage(sample_page_path.toAbsolutePath()))
    );

    final private static DataHolder OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS, Arrays.asList(
                    JiraConverterExtension.create(),
                    CrosspageLinkExtension.create()))
            .set(CURRENT_FILE_PATH, Path.of(""))
            .set(PAGES_STRUCTURE, TEST_PAGES_STRUCTURE)
            .set(EXTRACT_TITLE_STRATEGY, ExtractTitleStrategy.FROM_FILENAME)
            .toImmutable();


    public CrosspageLinkTitleFromFilenameSpecTest(@NotNull SpecExample example) {
        super(example, null, OPTIONS);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return getTestData(RESOURCE_LOCATION);
    }
}
