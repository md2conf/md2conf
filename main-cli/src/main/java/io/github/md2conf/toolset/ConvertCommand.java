package io.github.md2conf.toolset;

import io.github.md2conf.converter.ConfluencePageFactory;
import io.github.md2conf.converter.Converter;
import io.github.md2conf.converter.ExtractTitleStrategy;
import io.github.md2conf.converter.noop.NoopConverter;
import io.github.md2conf.indexer.DefaultIndexer;
import io.github.md2conf.indexer.Indexer;
import io.github.md2conf.indexer.IndexerConfigurationProperties;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.util.ModelReadWriteUtil;
import org.apache.commons.lang3.NotImplementedException;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.nio.file.Path;

import static io.github.md2conf.converter.Converter.Type.MD2WIKI;


@Command(name = "convert",
        description = "Convert files to `confluence-content-model` or from `confluence-content-model`")
public class ConvertCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--converter"}, description = "Valid values: ${COMPLETION-CANDIDATES}",
            defaultValue = "MD2WIKI",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private Converter.Type converter = MD2WIKI;

    @CommandLine.Option(names = {"-i", "--input-dir"}, required = true, description = "input directory")
    private Path inputDirectory;

    @CommandLine.Option(names = {"-o", "--output-dir"}, description = "output directory", defaultValue = ".md2conf/out")
    private Path outputDirectory;

//    @CommandLine.Option(names = {"-m", "--model"}, description = "output directory")
//    private String modelFileName;

    @CommandLine.Option(names = {"--file-extension"}, description = "file extension to index as confluence content pages")
    private String fileExtension = "wiki";

//    @CommandLine.Option(names = {"--include-pattern"}, description = "Include pattern in format of glob:** or regexp:.*. For syntax see javadoc of java.nio.file.FileSystem.getPathMatcher method")
//    private String includePattern = "glob:**";

    @CommandLine.Option(names = {"--exclude-pattern"}, description = "Exclude pattern in format of glob:** or regexp:.*. For syntax see javadoc of java.nio.file.FileSystem.getPathMatcher method")
    private String excludePattern = "glob:**/.*";

    @CommandLine.Option(names = {"-et", "--extract-title-strategy"}, description = "Strategy to extract title from file",
            defaultValue = "FROM_FIRST_HEADER",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private ExtractTitleStrategy extractTitleStrategy = ExtractTitleStrategy.FROM_FIRST_HEADER;

    private void initParameters() {
        if (outputDirectory == null) {
            outputDirectory = new File(inputDirectory.toFile(), ".md2conf/out").toPath();
        }
    }

    @Override
    public void run() {
        initParameters();

        IndexerConfigurationProperties indexerConfigurationProperties = new IndexerConfigurationProperties();
        indexerConfigurationProperties.setFileExtension(fileExtension);
//        indexerConfigurationProperties.setIncludePattern(includePattern);
        indexerConfigurationProperties.setExcludePattern(excludePattern);

        Indexer indexer = new DefaultIndexer(indexerConfigurationProperties);
        PagesStructure pagesStructure = indexer.indexPath(inputDirectory);

        ConfluencePageFactory confluencePageFactory = new ConfluencePageFactory(extractTitleStrategy);

        ConfluenceContentModel model = null;

        switch (converter){
            case MD2WIKI:
                throw new NotImplementedException();
            case NO:
                NoopConverter noopConverter = new NoopConverter(confluencePageFactory);
                model  = noopConverter.convert(pagesStructure);
        }

        ModelReadWriteUtil.saveConfluenceContentModelToFilesystem(model, outputDirectory);
    }

}
