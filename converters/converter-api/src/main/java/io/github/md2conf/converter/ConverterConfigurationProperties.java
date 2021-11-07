package io.github.md2conf.converter;

import java.nio.file.Path;

public class ConverterConfigurationProperties {

    private ExtractTitleStrategy extractTitleStrategy = ExtractTitleStrategy.FROM_FIRST_HEADER;

    private Path outputDir;


    public ExtractTitleStrategy getExtractTitleStrategy() {
        return extractTitleStrategy;
    }

    public void setExtractTitleStrategy(ExtractTitleStrategy extractTitleStrategy) {
        this.extractTitleStrategy = extractTitleStrategy;
    }

    public Path getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(Path outputDir) {
        this.outputDir = outputDir;
    }
}
