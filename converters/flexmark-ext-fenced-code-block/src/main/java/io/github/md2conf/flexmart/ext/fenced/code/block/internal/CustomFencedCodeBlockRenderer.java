package io.github.md2conf.flexmart.ext.fenced.code.block.internal;

import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import io.github.md2conf.flexmart.ext.fenced.code.block.CustomFencedCodeBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomFencedCodeBlockRenderer implements NodeRenderer {

    private static final Map<String, String> LANG_MAP;

    static {
        LANG_MAP = new HashMap<>();
        //identity mapping from confluence code names
        //https://confluence.atlassian.com/doc/code-block-macro-139390.html
        LANG_MAP.put("actionscript3", "actionscript3");
        LANG_MAP.put("applescript", "applescript");
        LANG_MAP.put("bash", "bash");
        LANG_MAP.put("c#", "c#");
        LANG_MAP.put("cpp", "cpp");
        LANG_MAP.put("css", "css");
        LANG_MAP.put("coldfusion", "coldfusion");
        LANG_MAP.put("delphi", "delphi");
        LANG_MAP.put("diff", "diff");
        LANG_MAP.put("erlang", "erlang");
        LANG_MAP.put("groovy", "groovy");
        LANG_MAP.put("xml", "xml");
        LANG_MAP.put("java", "java");
        LANG_MAP.put("jfx", "jfx");
        LANG_MAP.put("js", "js");
        LANG_MAP.put("text", "text");
        LANG_MAP.put("powershell", "powershell");
        LANG_MAP.put("python", "python");
        LANG_MAP.put("ruby", "ruby");
        LANG_MAP.put("sql", "sql");
        LANG_MAP.put("sass", "sass");
        LANG_MAP.put("scala", "scala");
        LANG_MAP.put("vb", "vb");
        LANG_MAP.put("yml", "yml");
        // some additional mappings from code names to confluence names
        LANG_MAP.put("javascript", "js");
        LANG_MAP.put("yaml", "yml");
        LANG_MAP.put("txt", "text");
    }


    @Override
    public @Nullable Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<>();
        set.add(new NodeRenderingHandler<>(CustomFencedCodeBlock.class, this::render));
        return set;
    }

    private void render(CustomFencedCodeBlock node, NodeRendererContext context, HtmlWriter html) {
        String language;
        if (node.getInfo() != null) {
            language = LANG_MAP.getOrDefault(node.getInfo().toString(), "");
        } else {
            language = "";
        }
        html.raw("{code");
        if (!language.isEmpty()) {
            html.raw(":language=" + language);
        }
        html.raw("}\n");
        html.raw(node.getChars());
        html.raw("{code}\n");
    }

    public static class Factory implements NodeRendererFactory {
        @NotNull
        public NodeRenderer apply(@NotNull DataHolder options) {
            return new CustomFencedCodeBlockRenderer();
        }
    }
}
