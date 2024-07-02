package com.github.andrew0030.pandora_core.client.shader.templating.action;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.AbstractTransformationProcessor;

public class ReplaceVar extends InsertionAction {
    String from, to;

    public ReplaceVar(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String afterInputVar(TemplateTransformation transformation, String type, String var) {
        if (!var.equals(from))
            return null;

        String typeDst = transformation.getVarType(to);

        String value = to;
        // to vec4
        if (typeDst.equals("vec3") && type.equals("vec4")) value = "vec4(" + value + ",1.0)";
        else if (typeDst.equals("vec2") && type.equals("vec4")) value = "vec4(" + value + ",0.0,1.0)";
        else if (typeDst.equals("float") && type.equals("vec4")) value = "vec4(" + value + ",0.0,0.0,1.0)";

        // to vec3
        else if (typeDst.equals("vec2") && type.equals("vec3")) value = "vec3(" + value + ",0.0)";
        else if (typeDst.equals("float") && type.equals("vec3")) value = "vec3(" + value + ",0.0,0.0)";

        // to vec2
        else if (typeDst.equals("float") && type.equals("vec2")) value = "vec2(" + value + ",0.0)";

        // between fp and int
        else if (typeDst.equals("vec2") && type.equals("ivec2"))
            value = "ivec2(floatBitsToInt(" + value + ".x),floatBitsToInt(" + value + ".y))";
        else if (typeDst.equals("ivec2") && type.equals("vec2"))
            value = "vec2(intBitsToFloat(" + value + ".x),intBitsToFloat(" + value + ".y))";

        return AbstractTransformationProcessor.TRANSFORM_INJECT
                .replace("%snowflake%", transformation.generateSnowflake())
                .replace("%type%", type)
                .replace("%variable%", var)
                .replace("%transform%", value);
    }
}
