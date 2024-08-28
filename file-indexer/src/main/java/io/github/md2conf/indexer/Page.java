package io.github.md2conf.indexer;

import java.nio.file.Path;
import java.util.List;

public interface Page {

    Path path();

    List<? extends Page> children();

    List<Path> attachments();

    boolean skipUpdate();

}
