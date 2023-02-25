package io.github.md2conf.title.processor.wiki;

public class WikiHeaderUtil {


    protected static boolean isConfluenceWikiHeaderLine(String v) {
        String s = v.trim();
        return (s.startsWith("h1.")
                || s.startsWith("h2.")
                || s.startsWith("h3."))
                && s.length()>3;
    }
}

