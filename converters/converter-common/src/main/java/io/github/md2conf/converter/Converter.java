package io.github.md2conf.converter;

import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;

import java.io.IOException;

public interface Converter {

    ConfluenceContentModel convert(PagesStructure pagesStructure) throws IOException;

    enum Type {
        NO,
        MD2WIKI
    }
}
