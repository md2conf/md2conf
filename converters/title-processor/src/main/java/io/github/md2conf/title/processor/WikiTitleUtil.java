package io.github.md2conf.title.processor;

public class WikiTitleUtil {


    protected static boolean isConfluenceWikiHeaderLine(String v) {
        String s = v.trim();
        return (s.startsWith("h1.")
                || s.startsWith("h2.")
                || s.startsWith("h3."))
                && s.length()>3;
    }
}

