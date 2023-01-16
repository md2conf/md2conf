package io.github.md2conf.converter.md2wiki.link;

import com.vladsch.flexmark.ast.LinkNodeBase;
import com.vladsch.flexmark.util.ast.Visitor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

class UrlCollectorVisitor<T extends LinkNodeBase> implements Visitor<T> {
    private final Set<String> urls;

    UrlCollectorVisitor() {
        urls = new HashSet<>();
    }

    @Override
    public void visit(@NotNull T node) {
        String url = node.getUrl().toStringOrNull();
        if (url != null) {
            urls.add(url);
        }
    }

    public Set<String> getUrls() {
        return urls;
    }
}
