package io.github.md2conf.title.processor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TitleProcessorOptions {
    private TitleExtractStrategy titleExtractStrategy;
    private String titlePrefix;
    private String titleSuffix;
    private boolean titleChildPrefixed;

}
