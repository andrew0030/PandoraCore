package com.github.andrew0030.pandora_core.client.shader.templating;

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;

import java.util.Map;

/**
 * A helper class containing mappings of vanilla variable names to iris/of variable names
 */
public class NameMapper {
    private static final Map<String, String> SHADER_MAPPING = new Object2ObjectRBTreeMap<>();
    private static final Map<String, String> IRIS_MAPPING = new Object2ObjectRBTreeMap<>();
    // TODO: proper optifine map, as most of these variables are illegal, and thus need to be switched out before loading the shader

    static {
        SHADER_MAPPING.put("UV0", "gl_MultiTexCoord0"); // texture coords
        SHADER_MAPPING.put("UV1", null); // TODO: of has weird overlay coord support
        SHADER_MAPPING.put("UV2", "gl_MultiTexCoord1"); // lightmap coords

        SHADER_MAPPING.put("Position", "gl_Vertex"); // position
        SHADER_MAPPING.put("Color",    "gl_Color"); // color
        SHADER_MAPPING.put("Normal",   "gl_Normal"); // normal

        IRIS_MAPPING.put("UV0", "iris_UV0");
        IRIS_MAPPING.put("UV1", "iris_UV1");
        IRIS_MAPPING.put("UV2", "iris_UV2");

        IRIS_MAPPING.put("Position", "iris_Position");
        IRIS_MAPPING.put("Color",    "iris_Color");
        IRIS_MAPPING.put("Normal",   "iris_Normal");
    }

    public static String assumeType(String type, String name) {
        switch (type) {
            case "float":
            case "vec2":
            case "vec3":
            case "vec4":

            case "int":
            case "ivec2":
            case "ivec3":
            case "ivec4":
                return type;
        }

        // TODO: validate iris/of
        switch (name) {
            case "Position":
                return "vec3";
            case "Color":
                return "vec4";
            case "UV0":
            case "UV2":
                return "vec2";
            case "UV1":
                return "ivec2";
            case "Normal":
                return "vec3";
        }

        return type;
    }
}
