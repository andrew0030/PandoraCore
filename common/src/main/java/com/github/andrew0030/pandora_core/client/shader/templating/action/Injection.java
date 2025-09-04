package com.github.andrew0030.pandora_core.client.shader.templating.action;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.utils.shader.ShaderFile;
import com.github.andrew0030.pandora_core.client.utils.shader.ShaderParser;
import com.github.andrew0030.pandora_core.client.utils.shader.ln.Line;

import java.util.HashMap;
import java.util.List;

public class Injection extends InsertionAction {
    String text;

    public Injection(String text) {
        this.text = text;
    }

    @Override
    public String headInjection(TemplateTransformation transformation) {
        return transformation.beforeHInject() + transformTypes() + transformation.afterHInject();
    }

    public void resolveTypes(HashMap<String, String> varTypes) {
        ShaderFile file = ShaderParser.parse(text);
        for (Line line : file.lines()) {
            String trim = line.text.trim();
            if (
                    trim.startsWith("in") ||
                            trim.startsWith("paco_per_instance") ||
                            trim.startsWith("uniform")
            ) {
                List<String> strs = line.resolveInputVar();

                if (strs.size() >= 3) {
                    varTypes.put(strs.get(2), strs.get(1));
                }
            }
        }
    }

    protected String transformMatrix(String first, String matrType, String name) {
        StringBuilder builder = new StringBuilder();

        char vecType = matrType.charAt(matrType.length() - 1);
        int matrWidth = (int) (matrType.charAt(3) - '0');

        for (int i = 0; i < matrWidth; i++) {
            builder.append(first).append(" ")
                    .append("vec").append(vecType).append(" ")
                    .append(name).append("_").append(i).append(";\n");
        }
        builder.append(matrType).append(" ")
                .append(name).append(" = ").append(matrType).append("(");
        for (int i = 0; i < matrWidth; i++) {
            builder.append(name).append("_").append(i);
            if (i != matrWidth - 1)
                builder.append(", ");
        }
        builder.append(");\n");

        return builder.toString();
    }

    public String transformTypes() {
        ShaderFile file = ShaderParser.parse(text);
        StringBuilder builder = new StringBuilder();
        for (Line line : file.lines()) {
            String trim = line.text.trim();
            if (
                    trim.startsWith("in") ||
                            trim.startsWith("paco_per_instance") ||
                            trim.startsWith("uniform")
            ) {
                List<String> strs = line.resolveInputVar();

                if (strs.size() >= 3) {
                    String type = strs.get(1);

                    if (type.startsWith("mat")) {
                        builder.append(transformMatrix(strs.get(0), strs.get(1), strs.get(2)));
                    } else {
                        for (int i = 0; i < strs.size(); i++) {
                            builder.append(strs.get(i)).append(" ");
                        }
                        builder.append(";\n");
                    }
                }
            }
        }
        return builder.toString();
    }
}
