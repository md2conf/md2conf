package io.github.md2conf.toolset.subcommand;

import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.format.options.HeadingStyle;
import io.github.md2conf.converter.view2md.View2MdConverter;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.toolset.ConvertCommand;
import io.github.md2conf.toolset.ConvertOldCommand;
import io.github.md2conf.toolset.LoggingMixin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import static io.github.md2conf.toolset.PublishCommand.loadConfluenceContentModel;


@CommandLine.Command(name = "view2md")
public class View2MdConvertCommand implements Runnable{
    private final static Logger logger = LoggerFactory.getLogger(ConvertOldCommand.class);

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.ArgGroup(exclusive = false, heading = "Format options for view2md converter:\n", order = 4)
    private View2MdConvertCommand.FormatOptions formatOptions;

    @Override
    public void run() {


    }

    private void convertView2Md() {
        ConfluenceContentModel model = loadContentModelFromPathOrDefault(formatOptions);
        View2MdConverter view2MdConverter = new View2MdConverter(formatOptions.outputDirectory, formatOptionsAsDataHolder(formatOptions));
        view2MdConverter.convert(model);
        logger.info("Converting to markdown result saved to {}", formatOptions.outputDirectory);
    }

    private static ConfluenceContentModel loadContentModelFromPathOrDefault(ConvertCommand.ConvertOptions convertOptions) {
        ConfluenceContentModel model;
            model = loadConfluenceContentModel(convertOptions.modelPath);

        return model;
    }

    private static DataHolder formatOptionsAsDataHolder(View2MdConvertCommand.FormatOptions formatOptions) {
        MutableDataSet mutableDataSet = new MutableDataSet();
        if (formatOptions!=null) {
            mutableDataSet.set(Formatter.RIGHT_MARGIN, formatOptions.markdownRightMargin);
            mutableDataSet.set(Formatter.HEADING_STYLE, formatOptions.markdownHeadingStyle);
        }
        return mutableDataSet;
    }


    public static class FormatOptions extends ConvertCommand.ConvertOptions {
        @CommandLine.Option(names = {"--markdown-right-margin"}, description = "Markdown right margin size")
        public Integer markdownRightMargin = 120;
        @CommandLine.Option(names = {"--markdown-heading-style"}, description = "Markdown heading style. Valid values: ${COMPLETION-CANDIDATES}")
        public HeadingStyle markdownHeadingStyle = HeadingStyle.ATX_PREFERRED;
    }
}
