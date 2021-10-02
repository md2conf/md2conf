package io.github.md2conf.converter;

import java.nio.file.Path;
import java.util.List;

public interface FileBasedPagesStructureProvider {

    FileBasedPagesStructure structure();

    interface FileBasedPagesStructure {

        List<? extends FileBasedPage> pages();
    }

    interface FileBasedPage {

        Path path();

        List<? extends FileBasedPage> children();
    }
}
