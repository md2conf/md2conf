package io.github.md2conf.converter.markdown;

import io.github.md2conf.converter.ConverterConfigurationProperties;

public class MarkdownConverterConfigurationProperties implements ConverterConfigurationProperties {

    private String inputDirectory;
    private String outputDirectory;
    private String ignorePattern;
    private Title title = new Title();


    public String getInputDirectory() {
        return inputDirectory;
    }

    public void setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getIgnorePattern() {
        return ignorePattern;
    }

    public void setIgnorePattern(String ignorePattern) {
        this.ignorePattern = ignorePattern;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public static class Title{
        private String prefix;
        //private String suffix;
        private TitleBuildStrategy buildFrom = TitleBuildStrategy.FILE_NAME;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public TitleBuildStrategy getBuildFrom() {
            return buildFrom;
        }

        public void setBuildFrom(TitleBuildStrategy buildFrom) {
            this.buildFrom = buildFrom;
        }
    }

    enum TitleBuildStrategy{FILE_NAME, FIRST_HEADING_IN_FILE}
}
