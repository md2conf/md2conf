package io.github.md2conf.maven.plugin;

import com.vladsch.flexmark.util.format.options.HeadingStyle;
import io.github.md2conf.confluence.client.OrphanRemovalStrategy;
import io.github.md2conf.confluence.client.PublishingStrategy;
import io.github.md2conf.indexer.ChildLayout;
import io.github.md2conf.indexer.OrphanFileStrategy;
import io.github.md2conf.title.processor.TitleExtractStrategy;
import io.github.md2conf.toolset.ConvertOldCommand;
import io.github.md2conf.toolset.IndexCommand;
import io.github.md2conf.toolset.PublishCommand;
import io.github.md2conf.toolset.subcommand.Md2WikiConvertCommand;
import io.github.md2conf.toolset.subcommand.View2MdConvertCommand;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static io.github.md2conf.toolset.ConvertOldCommand.ConverterType.MD2WIKI;

public abstract class AbstractMd2ConfMojo extends AbstractMojo {

    static final String PREFIX = "md2conf.";

    @Parameter(property = PREFIX + "skip", defaultValue = "false")
    protected boolean skip;

    @Parameter(property = PREFIX + "outputDirectory", defaultValue = "${project.build.directory}/md2conf", readonly = true)
    protected File outputDirectory;

    /// --- CONVERT options

    @Parameter(property = PREFIX + "inputDirectory")
    protected File inputDirectory;
    @Parameter(property = PREFIX + "converter")
    protected ConvertOldCommand.ConverterType converter = MD2WIKI;
    @Parameter(property = PREFIX + "fileExtension")
    protected String fileExtension = "md";
    @Parameter(property = PREFIX + "excludePattern")
    protected String excludePattern = "glob:**/.*";
    @Parameter(property = PREFIX + "indexerRootPage")
    protected String indexerRootPage = null;
    @Parameter(property = PREFIX + "childLayout")
    protected ChildLayout childLayout = ChildLayout.SUB_DIRECTORY;
    @Parameter(property = PREFIX + "orphanFileStrategy")
    protected OrphanFileStrategy orphanFileStrategy = OrphanFileStrategy.IGNORE;
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
    @Parameter(property = PREFIX + "plantumlCodeMacroEnable")
    protected Boolean plantumlCodeMacroEnable=false;
    @Parameter(property = PREFIX + "plantumlCodeMacroName")
    protected String plantumlCodeMacroName;
    @Parameter(property = PREFIX + "markdownRightMargin")
    protected Integer markdownRightMargin;
    @Parameter(property = PREFIX + "markdownHeadingStyle")
    protected HeadingStyle markdownHeadingStyle;

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
    @Parameter(property = PREFIX + "parentPagePublishingStrategy")
    protected PublishingStrategy parentPagePublishingStrategy = PublishingStrategy.APPEND_TO_ANCESTOR;
    @Parameter(property = PREFIX + "notifyWatchers")
    protected boolean notifyWatchers = false;
    @Parameter(property = PREFIX + "skipSslVerification")
    protected boolean skipSslVerification = false;
    @Parameter(property = PREFIX + "maxRequestsPerSecond")
    protected Double maxRequestsPerSecond;
    @Parameter(property = PREFIX + "connectionTimeToLive")
    protected Integer connectionTimeToLive;


    @NotNull
    protected Md2WikiConvertCommand.Md2WikiConvertOptions getConvertOptions() {
        Md2WikiConvertCommand.Md2WikiConvertOptions md2WikiConvertOptions = new Md2WikiConvertCommand.Md2WikiConvertOptions();
        md2WikiConvertOptions.converter = this.converter;
        md2WikiConvertOptions.outputDirectory = this.outputDirectory.toPath();
        md2WikiConvertOptions.titleExtract = this.titleExtract;
        md2WikiConvertOptions.titlePrefix = this.titlePrefix;
        md2WikiConvertOptions.titleSuffix = this.titleSuffix;
        md2WikiConvertOptions.titleChildPrefixed = this.titleChildPrefixed;
        md2WikiConvertOptions.titleRemoveFromContent = this.titleRemoveFromContent;
        md2WikiConvertOptions.plantumlCodeMacroEnable = this.plantumlCodeMacroEnable;
        md2WikiConvertOptions.plantumlCodeMacroName = this.plantumlCodeMacroName;
        return md2WikiConvertOptions;
    }

    @NotNull
    protected View2MdConvertCommand.FormatOptions getFormatOptions() {
        View2MdConvertCommand.FormatOptions formatOptions = new View2MdConvertCommand.FormatOptions();
        formatOptions.markdownRightMargin = this.markdownRightMargin;
        formatOptions.markdownHeadingStyle = this.markdownHeadingStyle;
        return formatOptions;
    }

    protected IndexCommand.IndexerOptions getIndexerOptions(){
        IndexCommand.IndexerOptions indexerOptions = new IndexCommand.IndexerOptions();
        indexerOptions.inputDirectory = this.inputDirectory.toPath();
        indexerOptions.fileExtension = this.fileExtension;
        indexerOptions.excludePattern = this.excludePattern;
        indexerOptions.indexerRootPage = this.indexerRootPage;
        indexerOptions.childLayout = this.childLayout;
        indexerOptions.orphanFileStrategy = this.orphanFileStrategy;
        return indexerOptions;
    }

    @NotNull
    protected PublishCommand.PublishOptions getPublishOptions() {
        PublishCommand.PublishOptions options = new PublishCommand.PublishOptions();
        options.versionMessage = this.versionMessage;
        options.orphanRemovalStrategy = this.orphanRemovalStrategy;
        options.parentPagePublishingStrategy = this.parentPagePublishingStrategy;
        options.notifyWatchers  = this.notifyWatchers;
        return options;
    }

    protected PublishCommand.ConfluenceOptions getConfluenceOptions(){
        PublishCommand.ConfluenceOptions options = new PublishCommand.ConfluenceOptions();
        options.confluenceUrl = this.confluenceUrl;
        options.username = this.username;
        options.password = this.password;
        options.spaceKey = this.spaceKey;
        options.parentPageTitle = this.parentPageTitle;
        options.skipSslVerification = this.skipSslVerification;
        options.maxRequestsPerSecond = this.maxRequestsPerSecond;
        options.connectionTimeToLive = this.connectionTimeToLive;
        return options;
    }

    public File getConfluenceContentModelPath() {
        return confluenceContentModelPath;
    }

}
