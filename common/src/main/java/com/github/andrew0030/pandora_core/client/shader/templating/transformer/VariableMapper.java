package com.github.andrew0030.pandora_core.client.shader.templating.transformer;

public interface VariableMapper {
    default String remap(String proposedType, String srcName) {
        return srcName;
    }
}
