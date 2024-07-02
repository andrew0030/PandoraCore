package com.github.andrew0030.pandora_core.client.shader.templating.transformer;

import com.github.andrew0030.pandora_core.client.shader.templating.NameMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.action.InsertionAction;
import com.github.andrew0030.pandora_core.client.utils.shader.ShaderFile;
import com.github.andrew0030.pandora_core.client.utils.shader.ln.Line;

import java.util.ArrayList;
import java.util.List;

public class VanillaTransformationProcessor extends AbstractTransformationProcessor {
    @Override
    public ShaderFile process(ShaderFile source, TemplateTransformation transformation) {
        ArrayList<Line> result = new ArrayList<>();

        boolean hitNonComment = false;

        for (Line line : source.lines()) {
            result.add(line);
            String trim = line.text.trim();

            if (trim.startsWith("#version")) {
                for (InsertionAction action : transformation.getActions()) {
                    String inject = action.headInjection(transformation);
                    if (inject != null) {
                        result.add(new Line(-1, inject));
                    }
                }
                hitNonComment = true;
            }

            if (trim.startsWith("in") || trim.startsWith("uniform")) {
                List<String> strs = line.resolveInputVar();

                if (strs.size() >= 3) {
                    String type = strs.get(1);
                    String name = strs.get(2);

                    for (InsertionAction action : transformation.getActions()) {
                        // TODO: track defines to make this a little more bullet proof
                        type = NameMapper.assumeType(type, name);

                        String inject = action.afterInputVar(transformation, type, name);
                        if (inject != null) {
                            result.add(new Line(-1, inject));
                        }
                    }
                }
            }

            if (line.type() != Line.LineType.COMMENT) {
                if (!hitNonComment) {
                    for (InsertionAction action : transformation.getActions()) {
                        String inject = action.headInjection(transformation);
                        if (inject != null) {
                            result.add(result.size() - 1, new Line(-1, inject));
                        }
                    }

                    hitNonComment = true;
                }
            }
        }

        return new ShaderFile(result);
    }
}
