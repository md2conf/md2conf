package io.github.md2conf.toolset;

import picocli.CommandLine;

@CommandLine.Command (name = "md2conf",
        helpCommand = true,
        hidden = true,
        description = "confluence-content-model is an abstraction to model confluence content on a filesystem." +
                "\n" +
                "### Confluence Content model\n" +
                "\n" +
                "Confluence Content is a collection of Confluence Pages.\n" +
                "\n" +
                "### Confluence Page\n" +
                "\n" +
                "Confluence Page has next attributes\n" +
                "\n" +
                "| Attribute       | Description                                |\n" +
                "|:----------------|:-------------------------------------------|\n" +
                "| title           | mandatory title                            |\n" +
                "| contentFilePath | mandatory content file path                |\n" +
                "| type            | \"storage\" or \"wiki\", see below for details |\n" +
                "| children        | optional collections of child pages        |\n" +
                "| attachments     | optional collections of attachments        |\n" +
                "| labels          | optional collections of labels             |\n" +
                "\n" +
                "#### Content Type\n" +
                "\n" +
                "Confluence support 2 types of markup \"storage\" or \"wiki\" to publish\n" +
                "pages using Confluence API. See Atlassian documentation for details:\n" +
                "\n" +
                "* [Confluence Storage Format](https://confluence.atlassian.com/doc/confluence-storage-format-790796544.html)\n" +
                "  \\- refered as \"storage\"\n" +
                "* [Confluence Wiki Markup](https://confluence.atlassian.com/doc/confluence-wiki-markup-251003035.html)\n" +
                "  \\- refered as \"wiki\""
)
public class ModelHelpCommand implements Runnable {
    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @Override
    public void run() {

    }
}
