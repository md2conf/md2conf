package io.github.md2conf.toolset.subcommand;

import com.vladsch.flexmark.util.format.options.HeadingStyle;
import io.github.md2conf.toolset.ConvertCommand;
import io.github.md2conf.toolset.LoggingMixin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(name = "view2md")
public class View2MdConvertCommand {
    private final static Logger logger = LoggerFactory.getLogger(ConvertCommand.class);

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    public static class FormatOptions{
        @CommandLine.Option(names = {"--markdown-right-margin"}, description = "Markdown right margin size")
        public Integer markdownRightMargin = 120;
        @CommandLine.Option(names = {"--markdown-heading-style"}, description = "Markdown heading style. Valid values: ${COMPLETION-CANDIDATES}")
        public HeadingStyle markdownHeadingStyle = HeadingStyle.ATX_PREFERRED;
    }
}
