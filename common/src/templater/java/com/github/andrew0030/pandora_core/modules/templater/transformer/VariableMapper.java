package com.github.andrew0030.pandora_core.modules.templater.transformer;

public interface VariableMapper {
    default String mapFrom(String proposedType, String name) {
        return name;
    }

    default String mapTo(String proposedType, String name) {
        return name;
    }
}
