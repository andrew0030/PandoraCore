package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.client.shader.templating.action.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class TemplateTransformationParser {
    public TemplateTransformationParser() {
    }

    public TemplateTransformation parse(ResourceLocation location, String text) {
        TemplateTransformation transformation = new TemplateTransformation(location);

        String blockType = null;
        String block = "";

        for (String s : text.split("\n")) {
            String trim = s.trim();

            if (blockType != null) {
                // end block directive (inject&undef) parsing
                if (trim.startsWith("#paco_end")) {
                    transformation.actions.add(switch (blockType) {
                        case "inject" -> new Injection(block);
                        case "undef" -> new Undef(block);
                        default -> throw new RuntimeException("???");
                    });
                    block = "";
                    blockType = null;
                } else {
                    block += s + "\n";
                }

                continue;
            }

            // skip comments
            // TODO: support block comments (/* */)
            if (trim.startsWith("//"))
                continue;

            // parse normal (single line) directives
            if (trim.startsWith("#paco_")) {
                int indx;
                String proc = trim.substring(0, (indx = trim.indexOf(" ")) == -1 ? trim.length() : indx);

                switch (proc) {
                    case "#paco_templated" -> {
                        String[] split = trim.split(" ");
                        transformation.templates.put(split[1], split[2]);
                    }
                    case "#paco_replace" -> {
                        String[] split = trim.split(" ");
                        transformation.actions.add(new ReplaceVar(split[1], split[2]));
                    }
                    case "#paco_transform" -> {
                        String[] split = trim.split(" ", 3);
                        transformation.actions.add(new TransformVar(split[1].replace(":", ""), split[2]));
                    }
                    case "#paco_inject" -> blockType = "inject";
                    case "#paco_undef" -> blockType = "undef";
                }
            }
        }

        transformation.lock();
        return transformation;
    }
}
