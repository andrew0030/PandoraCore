//package com.github.andrew0030.pandora_core.client.shader.templating.action;
//
//import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
//import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
//import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
//import tfc.glsl.base.GlslSegment;
//import tfc.glsl.base.GlslValue;
//import tfc.glsl.meta.VarSpecifier;
//
//import java.util.List;
//
//public class ReplaceVar extends InsertionAction {
//    VarSpecifier from;
//    GlslValue to;
//
//    public ReplaceVar(VarSpecifier from, GlslValue to) {
//        this.from = from;
//        this.to = to;
//    }
//
//    @Override
//    public List<GlslSegment> afterInputVar(VariableMapper mapper, TemplateTransformation transformation, String type, String var) {
//        String varMap = mapper.mapFrom(type, var);
//        if (!varMap.equals(from))
//            return null;
//
//        String typeDst = transformation.getVarType(to);
//
//        String value = to;
//        // to vec4
//        if (typeDst.equals("vec3") && type.equals("vec4")) value = "vec4(" + value + ",1.0)";
//        else if (typeDst.equals("vec2") && type.equals("vec4")) value = "vec4(" + value + ",0.0,1.0)";
//        else if (typeDst.equals("float") && type.equals("vec4")) value = "vec4(" + value + ",0.0,0.0,1.0)";
//
//            // to vec3
//        else if (typeDst.equals("vec2") && type.equals("vec3")) value = "vec3(" + value + ",0.0)";
//        else if (typeDst.equals("float") && type.equals("vec3")) value = "vec3(" + value + ",0.0,0.0)";
//
//            // to vec2
//        else if (typeDst.equals("float") && type.equals("vec2")) value = "vec2(" + value + ",0.0)";
//
//            // between fp and int
//        else if (typeDst.equals("vec2") && type.equals("ivec2"))
//            value = "ivec2(floatBitsToInt(" + value + ".x),floatBitsToInt(" + value + ".y))";
//        else if (typeDst.equals("ivec2") && type.equals("vec2"))
//            value = "vec2(intBitsToFloat(" + value + ".x),intBitsToFloat(" + value + ".y))";
//
//        return TransformationProcessor.TRANSFORM_INJECT
//                .replace("%snowflake%", transformation.generateSnowflake())
//                .replace("%type%", type)
//                .replace("%variable%", var)
//                .replace("%transform%", value);
//    }
//}
