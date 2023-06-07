package io.github.md2conf.converter.md2wiki.attachment;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.jira.converter.JiraConverterExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.misc.Extension;
import io.github.md2conf.flexmart.ext.confluence.macros.ConfluenceMacroExtension;
import io.github.md2conf.flexmart.ext.crosspage.links.CrosspageLinkExtension;
import io.github.md2conf.flexmart.ext.local.attachments.LocalAttachmentLinkExtension;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


class ImageUrlCollectorVisitorTest {

    public MutableDataSet getFlexmarkExtensions() {
        List<Extension> extensions = Arrays.asList(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                LocalAttachmentLinkExtension.create(),
                CrosspageLinkExtension.create(),
                ConfluenceMacroExtension.create(),
                JiraConverterExtension.create()
        );
        return new MutableDataSet().set(Parser.EXTENSIONS, extensions);
    }

    @Test
    void test_extract_link_local_image_relative_path() throws IOException {
        Path path = Paths.get("src/test/resources/markdown_examples/local_image_relative_path.md");
        String markdown = FileUtils.readFileToString(path.toFile(), Charset.defaultCharset());
        Parser parser = Parser.builder(getFlexmarkExtensions()).build();
        Node document = parser.parse(markdown);
        Set<String> set = ImageUrlUtil.collectUrlsOfImages(document);
        Assertions.assertThat(set).hasSize(1);
        Assertions.assertThat(set).contains("gif.gif");
    }

    @Test
    void test_extract_link_local_image_absolute_path() throws IOException {
        Path path = Paths.get("src/test/resources/markdown_examples/local_image_absolute_path.md");
        String markdown = FileUtils.readFileToString(path.toFile(), Charset.defaultCharset());
        Parser parser = Parser.builder(getFlexmarkExtensions()).build();
        Node document = parser.parse(markdown);
        Set<String> set = ImageUrlUtil.collectUrlsOfImages(document);
        Assertions.assertThat(set).hasSize(1);
        Assertions.assertThat(set).contains("/tmp/image.png");
    }

    @Test
    void test_extract_link_local_image_remote_url() throws IOException {
        Path path = Paths.get("src/test/resources/markdown_examples/local_image_remote_url.md");
        String markdown = FileUtils.readFileToString(path.toFile(), Charset.defaultCharset());
        Parser parser = Parser.builder(getFlexmarkExtensions()).build();
        Node document = parser.parse(markdown);
        Set<String> set = ImageUrlUtil.collectUrlsOfImages(document);
        Assertions.assertThat(set).hasSize(1);
        Assertions.assertThat(set).contains("http://link.com/image.png");
    }

    @Test
    void test_extract_links_to_local_image_and_http() throws IOException {
        Path path = Paths.get("src/test/resources/markdown_examples/image_and_urls.md");
        String markdown = FileUtils.readFileToString(path.toFile(), Charset.defaultCharset());
        Parser parser = Parser.builder(getFlexmarkExtensions()).build();
        Node document = parser.parse(markdown);
        Set<String> set = ImageUrlUtil.collectUrlsOfImages(document);
        Assertions.assertThat(set).hasSize(1);
        Assertions.assertThat(set).contains("sample.gif");
    }

}