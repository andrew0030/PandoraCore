package com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl;

import java.util.HashMap;
import java.util.Map;

public class TransformationContext {
    Map<String, String> methodNames = new HashMap<>();

    public String getFunc(String string) {
        return methodNames.get(string);
    }

    public void setFuncName(String fromName, String toName) {
        methodNames.put(fromName, toName);
    }
}
