package io.github.md2conf.flexmart.ext.local.attachments;

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

import static io.github.md2conf.flexmart.ext.local.attachments.LocalAttachmentLinkExtension.CURRENT_FILE_PATH;

public class ComboLocalAttachmentSpecTest extends RendererSpecTest {
    final private static String SPEC_RESOURCE = "/local_attachment_spec_test.md";
    final public static @NotNull ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(SPEC_RESOURCE);
    final private static DataHolder OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS, Arrays.asList(
                    JiraConverterExtension.create(),
                    LocalAttachmentLinkExtension.create()))
            .set(CURRENT_FILE_PATH, Path.of(""))
            .toImmutable();

    public ComboLocalAttachmentSpecTest(@NotNull SpecExample example) {
        super(example, null, OPTIONS);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return getTestData(RESOURCE_LOCATION);
    }
}
