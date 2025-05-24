package com.github.andrew0030.pandora_core.client.ctm;

import com.github.andrew0030.pandora_core.client.ctm.types.BaseCTMType;
import com.github.andrew0030.pandora_core.client.ctm.types.FullCTMType;
import com.github.andrew0030.pandora_core.client.ctm.types.HorizontalCTMType;
import com.github.andrew0030.pandora_core.client.ctm.types.VerticalCTMType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for registering and retrieving simple CTM (Connected Texture Mapping) types.
 */
public class CTMTypeManager {
    public static final Map<String, BaseCTMType> CTM_TYPES = new HashMap<>();

    static {
        CTMTypeManager.register("full", new FullCTMType());
        CTMTypeManager.register("horizontal", new HorizontalCTMType());
        CTMTypeManager.register("vertical", new VerticalCTMType());
    }

    /**
     * Registers a new CTM (Connected Texture Mapping) type with the given name.<br/>
     * This method associates a unique string key with a {@link BaseCTMType} implementation.
     *
     * @param name the unique string identifier for the CTM type.
     * @param type the {@link BaseCTMType} instance representing the CTM behavior.
     * @throws IllegalArgumentException if a CTM type is already registered with the given name.
     */
    public static void register(String name, BaseCTMType type) {
        if (CTM_TYPES.put(name, type) != null)
            throw new IllegalArgumentException("Attempted to register CTM type with existing key: " + name);
    }

    /**
     * Retrieves a CTM type by its name.
     *
     * @param name the unique identifier of the CTM type.
     * @return the CTM type instance, or {@code null} if none is found.
     */
    public static @Nullable BaseCTMType get(String name) {
        return CTM_TYPES.get(name);
    }
}