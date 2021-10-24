package io.github.md2conf.toolset.config;

import io.github.md2conf.confluence.client.ConfluenceClientConfigurationProperties;
import io.github.md2conf.converter.markdown.MarkdownConverterConfigurationProperties;

public class ConfigFactory {

    public static ConfluenceClientConfigurationProperties aDefaultConfluenceClientConfigurationProperties(){
        return new ConfluenceClientConfigurationProperties();
    }

    public static MarkdownConverterConfigurationProperties aDefaultMarkdownConverterConfigurationProperties(){
        return new MarkdownConverterConfigurationProperties();
    }


}
