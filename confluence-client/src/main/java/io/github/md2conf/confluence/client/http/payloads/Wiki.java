package io.github.md2conf.confluence.client.http.payloads;

import io.github.md2conf.confluence.client.support.RuntimeUse;

public class Wiki {
    private String value;

    @RuntimeUse
    public String getRepresentation() {
        return "wiki";
    }

    @RuntimeUse
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
