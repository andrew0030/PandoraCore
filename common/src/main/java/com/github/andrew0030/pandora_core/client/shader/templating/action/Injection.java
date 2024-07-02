package com.github.andrew0030.pandora_core.client.shader.templating.action;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
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
        return transformation.beforeHInject() + this.text + transformation.afterHInject();
    }

    public void resolveTypes(HashMap<String, String> varTypes) {
        for (Line line : ShaderParser.parse(text).lines()) {
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
}
