package io.github.md2conf.command.subcommand;

import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.format.options.HeadingStyle;
import io.github.md2conf.command.ConvertCommand;
import io.github.md2conf.command.LoggingMixin;
import io.github.md2conf.converter.view2md.View2MdConverter;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.util.ModelFilesystemUtil;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.nio.file.Path;


@CommandLine.Command(name = "view2md")
@Slf4j
public class View2MdConvertCommand implements Runnable{

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1", heading = "view2md converting options:\n")
    View2MdConvertOptions view2MdConvertOptions;
    @CommandLine.ArgGroup(exclusive = false, heading = "markdown format options:\n")
    MarkdownFormatOptions markdownFormatOptions;


    @Override
    public void run() {
        var localMarkdownFormatOptions = markdownFormatOptions==null? new View2MdConvertCommand.MarkdownFormatOptions(): markdownFormatOptions;
        convertView2Md(this.view2MdConvertOptions, localMarkdownFormatOptions);
    }

    public static void convertView2Md(View2MdConvertOptions view2MdConvertOptions, MarkdownFormatOptions markdownFormatOptions) {
        ConfluenceContentModel model = ModelFilesystemUtil.readModel(view2MdConvertOptions.modelPath);
        View2MdConverter view2MdConverter = new View2MdConverter(view2MdConvertOptions.outputDirectory, formatOptionsAsDataHolder(markdownFormatOptions));
        view2MdConverter.convert(model);
        log.info("Converting to markdown result saved to {}", view2MdConvertOptions.outputDirectory);
    }

    private static DataHolder formatOptionsAsDataHolder(MarkdownFormatOptions view2MdConvertOptions) {
        MutableDataSet mutableDataSet = new MutableDataSet();
        if (view2MdConvertOptions !=null) {
            mutableDataSet.set(Formatter.RIGHT_MARGIN, view2MdConvertOptions.markdownRightMargin);
            mutableDataSet.set(Formatter.HEADING_STYLE, view2MdConvertOptions.markdownHeadingStyle);
        }
        return mutableDataSet;
    }

    public static class View2MdConvertOptions extends ConvertCommand.ConvertOptions {
        @CommandLine.Option(names = { "--model-path"}, required = true, description = "Model path directory")
        public Path modelPath;
    }
    public static class MarkdownFormatOptions {
        @CommandLine.Option(names = {"--markdown-right-margin"}, description = "Markdown right margin size")
        public Integer markdownRightMargin = 120;
        @CommandLine.Option(names = {"--markdown-heading-style"}, description = "Markdown heading style. Valid values: ${COMPLETION-CANDIDATES}")
        public HeadingStyle markdownHeadingStyle = HeadingStyle.ATX_PREFERRED;
    }

}
