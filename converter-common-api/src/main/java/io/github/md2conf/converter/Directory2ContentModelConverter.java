package io.github.md2conf.converter;

import io.github.md2conf.model.ConfluenceContent;

import java.nio.file.Path;

public interface Directory2ContentModelConverter {
    ConfluenceContent convert(Path file);

}
