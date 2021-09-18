package io.github.md2conf.confluence.client.http.payloads;

import io.github.md2conf.confluence.client.support.RuntimeUse;

public class Label {

    private final String name;

    public Label(String name) {
        this.name = name;
    }

    @RuntimeUse
    public String getName() {
        return this.name;
    }

}