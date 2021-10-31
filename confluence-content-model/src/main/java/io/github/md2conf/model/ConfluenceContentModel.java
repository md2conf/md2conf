package io.github.md2conf.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfluenceContentModel {

    public static String DEFAULT_FILE_NAME = "confluence-content-model.json";

    private List<ConfluencePage> pages = new ArrayList<>();

    public List<ConfluencePage> getPages() {
        return pages;
    }

    public void setPages(List<ConfluencePage> pages) {
        this.pages = pages;
    }

    public ConfluenceContentModel() {
    }

    public ConfluenceContentModel(List<ConfluencePage> pages) {
        this.pages = pages;
    }

    public ConfluenceContentModel(ConfluencePage...pages){
        this.pages = Arrays.stream(pages).collect(Collectors.toList());
    }

    public enum Type {
        STORAGE, WIKI
    }
}
