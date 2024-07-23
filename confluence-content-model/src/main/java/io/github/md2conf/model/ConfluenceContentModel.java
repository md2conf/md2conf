package io.github.md2conf.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class ConfluenceContentModel {

    private List<ConfluencePage> pages = new ArrayList<>();

    public ConfluenceContentModel() {
    }

    public ConfluenceContentModel(List<ConfluencePage> pages) {
        this.pages = pages;
    }

    public ConfluenceContentModel(ConfluencePage...pages){
        this.pages = Arrays.stream(pages).collect(Collectors.toList());
    }

    public enum Type {
        STORAGE, WIKI, VIEW
    }
}
