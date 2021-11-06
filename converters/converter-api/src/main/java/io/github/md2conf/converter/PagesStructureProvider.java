package io.github.md2conf.converter;

import java.nio.file.Path;
import java.util.List;

/**
 * Helper interface to provide pages hierarchy structure.
 */
public interface PagesStructureProvider {

    PagesStructure structure();

    interface PagesStructure {

        List<? extends Page> pages();
    }

    interface Page {

        Path path();

        List<? extends Page> children();

        void addChild(Page page);
    }
}
