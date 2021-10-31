package io.github.md2conf.toolset;

import io.github.md2conf.converter.markdown.MarkdownConverterConfigurationProperties;
import io.github.md2conf.converter.markdown.MarkdownMainConverter;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.nio.file.Path;



@Command(name = "convert",
        description = "Convert files to `confluence-content-model` or from `confluence-content-model`")
public class ConvertCommand implements Runnable {

//    @CommandLine.Option(names = {"-c", "--converter"}, description = "converter",
//            defaultValue = "MARKDOWN_TO_WIKI",
//            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
//            hidden = true)
//    private Converter converter = MARKDOWN_TO_WIKI;

    @CommandLine.Option(names = {"-i", "--input-dir"}, required = true, description = "input directory")
    private Path inputDirectory;

    @CommandLine.Option(names = {"-o", "--output-dir"}, description = "output directory")
    private Path outputDirectory;

    private void initParameters() {
        if (outputDirectory == null) {
            outputDirectory = new File(inputDirectory.toFile(), ".md2conf/out").toPath();
        }
    }

    @Override
    public void run() {
        initParameters();
        //todo implement right work with config

        MarkdownConverterConfigurationProperties converterConfigurationProperties = new MarkdownConverterConfigurationProperties();
        converterConfigurationProperties.setInputDirectory(inputDirectory.toString());
        converterConfigurationProperties.setOutputDirectory(outputDirectory.toString());
        MarkdownMainConverter mainConverter = new MarkdownMainConverter();
        mainConverter.convertAndSave(converterConfigurationProperties);
    }


//    public enum Converter {
//        MARKDOWN_TO_WIKI(new MarkdownMainConverter()),
//        WIKI_TO_MARKDOWN(null); //not implemented
//
//        private final AbstractConverter<?> converter;
//
//        Converter(AbstractConverter<?> converter) {
//            this.converter = converter;
//        }
//
//        public AbstractConverter<?> getConverter() {
//            return converter;
//        }
//    }


}
