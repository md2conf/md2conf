package io.github.md2conf.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

/**
 * @author Alain Sahli
 */
public class ConfluencePage {

    private String title;
    private String contentFilePath;

    private ConfluenceContentModel.Type type = ConfluenceContentModel.Type.STORAGE;
    private List<ConfluencePage> children = new ArrayList<>();
    private Map<String, String> attachments = new HashMap<>();
    private List<String> labels = new ArrayList<>();

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentFilePath() {
        return this.contentFilePath;
    }

    public void setContentFilePath(String contentFilePath) {
        this.contentFilePath = contentFilePath;
    }

    public ConfluenceContentModel.Type getType() {
        return type;
    }

    public void setType(ConfluenceContentModel.Type type) {
        this.type = type;
    }

    public List<ConfluencePage> getChildren() {
        if (this.children == null) {
            return emptyList();
        } else {
            return this.children;
        }
    }

    public void setChildren(List<ConfluencePage> children) {
        this.children = children;
    }

    public Map<String, String> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    public List<String> getLabels() {
        return this.labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

}
