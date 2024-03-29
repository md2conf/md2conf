package io.github.md2conf.flexmart.ext.plantuml.code.macro.internal;

import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import io.github.md2conf.flexmart.ext.plantuml.code.macro.PlantUmlCodeMacro;
import io.github.md2conf.flexmart.ext.plantuml.code.macro.PlantUmlCodeMacroExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class PlantUmlCodeMacroRenderer implements NodeRenderer {

    private final String macroName;

    public PlantUmlCodeMacroRenderer(String macroName) {
        this.macroName = macroName;
    }

    @Override
    public @Nullable Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<>();
        set.add(new NodeRenderingHandler<>(PlantUmlCodeMacro.class, this::render));
        return set;
    }

    private void render(PlantUmlCodeMacro node, NodeRendererContext context, HtmlWriter html) {
        html.raw("{").raw(macroName).raw("}\n");
        html.raw(node.getChars());
        html.raw("{").raw(macroName).raw("}\n");
    }

    public static class Factory implements NodeRendererFactory {

        @NotNull
        public NodeRenderer apply(@NotNull DataHolder options) {
            String macroName = (String) options.getAll().get(PlantUmlCodeMacroExtension.CONFLUENCE_PLANTUML_MACRO);
            if (macroName==null){
                macroName = PlantUmlCodeMacroExtension.CONFLUENCE_PLANTUML_MACRO.getDefaultValue();
            }
            return new PlantUmlCodeMacroRenderer(macroName);
        }
    }
}
