package io.github.md2conf.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyList;

/**
 * @author Alain Sahli
 */
@Setter
@Getter
public class ConfluencePage {

    private String title;
    private String contentFilePath;
    private ConfluenceContentModel.Type type = ConfluenceContentModel.Type.STORAGE;
    private List<ConfluencePage> children = new ArrayList<>();
    private Map<String, String> attachments = new HashMap<>();
    private List<String> labels = new ArrayList<>();

    public List<ConfluencePage> getChildren() {
        return Objects.requireNonNullElse(this.children, emptyList());
    }

}
