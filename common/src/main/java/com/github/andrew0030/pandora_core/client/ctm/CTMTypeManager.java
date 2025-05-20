package com.github.andrew0030.pandora_core.client.ctm;

import com.github.andrew0030.pandora_core.client.ctm.types.BaseCTMType;
import com.github.andrew0030.pandora_core.client.ctm.types.FullCTMType;
import com.github.andrew0030.pandora_core.client.ctm.types.HorizontalCTMType;
import com.github.andrew0030.pandora_core.client.ctm.types.VerticalCTMType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CTMTypeManager {
    public static final Map<String, BaseCTMType> CTM_TYPES = new HashMap<>();

    static {
        CTMTypeManager.registerCTMType("full", new FullCTMType());
        CTMTypeManager.registerCTMType("horizontal", new HorizontalCTMType());
        CTMTypeManager.registerCTMType("vertical", new VerticalCTMType());
    }

    public static void registerCTMType(String name, BaseCTMType type) {
        if (CTM_TYPES.put(name, type) != null)
            throw new IllegalArgumentException("Attempted to register CTM type with existing key: " + name);
    }

    public static @Nullable BaseCTMType getCTMType(String key) {
        return CTM_TYPES.get(key);
    }
}