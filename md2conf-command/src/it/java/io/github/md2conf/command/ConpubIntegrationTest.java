package io.github.md2conf.command;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.assertj.core.api.Assertions.assertThat;

class ConpubIntegrationTest extends AbstractContainerTestBase{

    @TempDir
    private Path outputPath;

    @Test
    public void convert_and_publish() {
        assertThat(super.pageIdBy(PARENT_PAGE_TITLE)).isNullOrEmpty();
        deletePageIfExists("Sample");
        deletePageIfExists("Appendix 01");

        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        String inputPath = "src/it/resources/several-pages";
        String[] args = ArrayUtils.addAll(commonConvertAndPublishArgs(),
                "-i", inputPath,
                "-o", outputPath.toString());
        int exitCode = cmd.execute(args);
        assertThat(exitCode).isEqualTo(0);

        // check sample page
        String id = super.pageIdBy("Sample");
        assertThat(id).isNotNull();
        assertThat(pageBodyStorageById(id)).doesNotContain("<h1>Sample</h1>"); //first header removed from content and becomes the title
        assertThat(pageBodyStorageById(id)).contains("<ac:image><ri:attachment ri:filename=\"sample.gif\" /></ac:image>");
        assertThat(pageAttachmentsTitles(id)).contains("attachment.txt", "sample.gif" ).hasSize(2);

        // check appendix page
        String appendixId = super.pageIdBy("Appendix 01");
        assertThat(appendixId).isNotNull();
        assertThat(pageBodyStorageById(appendixId)).doesNotContain("<h1>Appendix 01</h1>"); //first header removed from content and becomes the title
        assertThat(pageAttachmentsTitles(appendixId)).isEmpty();

    }

    @Test
    public void conpub_twice_and_check_page_updates() throws IOException {
        assertThat(super.pageIdBy(PARENT_PAGE_TITLE)).isNullOrEmpty();
        deletePageIfExists("Sample A");
        deletePageIfExists("Page to skip update");

        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        String inputPath = "src/it/resources/skip-update-sample";
        String[] args = ArrayUtils.addAll(commonConvertAndPublishArgs(),
                "-i", inputPath,
                "-o", outputPath.toString());
        int exitCode = cmd.execute(args);
        assertThat(exitCode).isEqualTo(0);

        // check sample page
        String id = super.pageIdBy("Sample A");
        assertThat(id).isNotNull();

        // check second page
        String appendixId = super.pageIdBy("Page to skip update");
        assertThat(appendixId).isNotNull();
        assertThat(pageBodyStorageById(id)).doesNotContain("the text"); //first header removed from content and becomes the title

        Files.write(Paths.get(inputPath+"/page_to_skip_update.md"), "\n\nthe text".getBytes(), StandardOpenOption.APPEND);


        int exitCode2 = cmd.execute(args);
        assertThat(exitCode2).isEqualTo(0);

        // check second page
        appendixId = super.pageIdBy("Page to skip update");
        assertThat(appendixId).isNotNull();
        assertThat(pageBodyStorageById(id)).doesNotContain("the text"); //first header removed from content and becomes the title

    }

    private String[] commonConvertAndPublishArgs() {
        String[] args = new String[]{"conpub"};
        args = ArrayUtils.addAll(args, CLI_OPTIONS);
        args = ArrayUtils.addAll(args, "-url", confluenceBaseUrl());
        return args;
    }
}
