package io.github.md2conf.converter;

import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;

public interface Converter {

    ConfluenceContentModel convert(PagesStructure pagesStructure);

    enum Type {
        NO,
        MD2WIKI
    }
}
