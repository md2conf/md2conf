package io.github.md2conf.model;

import io.github.md2conf.confluence.client.support.RuntimeUse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class ConfluenceContent {
    private List<ConfluencePage> pages = new ArrayList<>();

    public List<ConfluencePage> getPages() {
        return pages;
    }

    public void setPages(List<ConfluencePage> pages) {
        this.pages = pages;
    }

    public ConfluenceContent() {
    }

    public ConfluenceContent(List<ConfluencePage> pages) {
        this.pages = pages;
    }

    public ConfluenceContent(ConfluenceContent.ConfluencePage...pages){
        this.pages = Arrays.stream(pages).collect(Collectors.toList());
    }

    /**
     * @author Alain Sahli
     */
    public static class ConfluencePage {

        private String title;
        private String contentFilePath;
        private List<ConfluencePage> children = new ArrayList<>();
        private Map<String, String> attachments = new HashMap<>();
        private List<String> labels = new ArrayList<>();

        public String getTitle() {
            return this.title;
        }

        @RuntimeUse
        public void setTitle(String title) {
            this.title = title;
        }

        public String getContentFilePath() {
            return this.contentFilePath;
        }

        @RuntimeUse
        public void setContentFilePath(String contentFilePath) {
            this.contentFilePath = contentFilePath;
        }

        public List<ConfluencePage> getChildren() {
            if (this.children == null) {
                return emptyList();
            } else {
                return this.children;
            }
        }

        @RuntimeUse
        public void setChildren(List<ConfluencePage> children) {
            this.children = children;
        }

        public Map<String, String> getAttachments() {
            return this.attachments;
        }

        @RuntimeUse
        public void setAttachments(Map<String, String> attachments) {
            this.attachments = attachments;
        }

        public List<String> getLabels() {
            return this.labels;
        }

        @RuntimeUse
        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

    }
}
