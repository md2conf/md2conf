package io.github.md2conf.flexmart.ext.crosspage.links;

import com.vladsch.flexmark.core.test.util.RendererSpecTest;
import com.vladsch.flexmark.jira.converter.JiraConverterExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jetbrains.annotations.NotNull;
import org.junit.runners.Parameterized;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.github.md2conf.flexmart.ext.crosspage.links.CrosspageLinkExtension.CURRENT_FILE_PATH;
import static io.github.md2conf.flexmart.ext.crosspage.links.CrosspageLinkExtension.TITLE_MAP;

public class CrosspageLinkSpecTest extends RendererSpecTest {
    final private static String SPEC_RESOURCE = "/crosspage_link_title_spec_test.md";
    final public static @NotNull ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(SPEC_RESOURCE);

    final private static Map<Path,String> titleMap =
            Map.of(Path.of("src/test/resources/sample_page.md").toAbsolutePath(), "sample_page");

    final private static DataHolder OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS, Arrays.asList(
                    JiraConverterExtension.create(),
                    CrosspageLinkExtension.create()))
            .set(CURRENT_FILE_PATH, Path.of(""))
            .set(TITLE_MAP, titleMap)
            .toImmutable();


    public CrosspageLinkSpecTest(@NotNull SpecExample example) {
        super(example, null, OPTIONS);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return getTestData(RESOURCE_LOCATION);
    }
}
