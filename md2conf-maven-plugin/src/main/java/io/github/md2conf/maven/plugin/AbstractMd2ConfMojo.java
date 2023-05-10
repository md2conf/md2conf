package io.github.md2conf.maven.plugin;

import io.github.md2conf.confluence.client.OrphanRemovalStrategy;
import io.github.md2conf.title.processor.TitleExtractStrategy;
import io.github.md2conf.toolset.ConvertCommand;
import io.github.md2conf.toolset.PublishCommand;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static io.github.md2conf.toolset.ConvertCommand.ConverterType.MD2WIKI;

public abstract class AbstractMd2ConfMojo extends AbstractMojo {

    static final String PREFIX = "md2conf.";

    @Parameter(property = PREFIX + "skip", defaultValue = "false")
    protected boolean skip;

    @Parameter(defaultValue = "${project.build.directory}/md2conf", readonly = true)
    protected File outputDirectory;

    /// --- CONVERT options

    @Parameter(property = PREFIX + "inputDirectory")
    protected File inputDirectory;
    @Parameter(property = PREFIX + "converter")
    protected ConvertCommand.ConverterType converter = MD2WIKI;
    @Parameter(property = PREFIX + "fileExtension")
    protected String fileExtension = "md";
    @Parameter(property = PREFIX + "excludePattern")
    protected String excludePattern = "glob:**/.*";
    @Parameter(property = PREFIX + "indexerRootPage")
    protected String indexerRootPage = null;
    @Parameter(property = PREFIX + "titleExtract")
    protected TitleExtractStrategy titleExtract = TitleExtractStrategy.FROM_FIRST_HEADER;
    @Parameter(property = PREFIX + "titlePrefix")
    protected String titlePrefix;
    @Parameter(property = PREFIX + "titleSuffix")
    protected String titleSuffix;
    @Parameter(property = PREFIX + "titleChildPrefixed")
    protected boolean titleChildPrefixed;
    @Parameter(property = PREFIX + "titleRemoveFromContent")
    protected Boolean titleRemoveFromContent;
    
    /// --- PUBLISH options

    @Parameter(property = PREFIX + "confluenceUrl")
    protected String confluenceUrl;
    @Parameter(property = PREFIX + "username")
    protected String username;
    @Parameter(property = PREFIX + "password")
    protected String password;
    @Parameter(property = PREFIX + "spaceKey")
    protected String spaceKey;
    @Parameter(property = PREFIX + "parentPageTitle")
    protected String parentPageTitle;
    @Parameter(property = PREFIX + "confluenceContentModelPath")
    protected File confluenceContentModelPath;
    @Parameter(property = PREFIX + "versionMessage")
    protected String versionMessage = "Published by md2conf";
    @Parameter(property = PREFIX + "orphanRemovalStrategy")
    protected OrphanRemovalStrategy orphanRemovalStrategy = OrphanRemovalStrategy.KEEP_ORPHANS;
    @Parameter(property = PREFIX + "notifyWatchers")
    protected boolean notifyWatchers = false;
    @Parameter(property = PREFIX + "skipSslVerification")
    protected boolean skipSslVerification = false;
    @Parameter(property = PREFIX + "maxRequestsPerSecond")
    protected Double maxRequestsPerSecond;


    @NotNull
    protected ConvertCommand.ConvertOptions getConvertOptions() {
        ConvertCommand.ConvertOptions convertOptions = new ConvertCommand.ConvertOptions();
        convertOptions.converter = this.converter;
        convertOptions.outputDirectory = this.outputDirectory.toPath();
        convertOptions.inputDirectory = this.inputDirectory.toPath();
        convertOptions.fileExtension = this.fileExtension;
        convertOptions.excludePattern = this.excludePattern;
        convertOptions.indexerRootPage = this.indexerRootPage;
        convertOptions.titleExtract = this.titleExtract;
        convertOptions.titlePrefix = this.titlePrefix;
        convertOptions.titleSuffix = this.titleSuffix;
        convertOptions.titleChildPrefixed = this.titleChildPrefixed;
        convertOptions.titleRemoveFromContent = this.titleRemoveFromContent;
        return convertOptions;
    }

    @NotNull
    protected PublishCommand.PublishOptions getPublishOptions() {
        PublishCommand.PublishOptions publishOptions = new PublishCommand.PublishOptions();
        publishOptions.confluenceUrl = this.confluenceUrl;
        publishOptions.username = this.username;
        publishOptions.password = this.password;
        publishOptions.spaceKey = this.spaceKey;
        publishOptions.parentPageTitle = this.parentPageTitle;
        publishOptions.versionMessage = this.versionMessage;
        publishOptions.orphanRemovalStrategy = this.orphanRemovalStrategy;
        publishOptions.notifyWatchers  = this.notifyWatchers;
        publishOptions.skipSslVerification = this.skipSslVerification;
        publishOptions.maxRequestsPerSecond = this.maxRequestsPerSecond;
        return publishOptions;
    }


}
