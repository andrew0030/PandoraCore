package com.github.andrew0030.pandora_core.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class PaCoKeyMappings {

    public static final List<KeyMapping> KEY_MAPPINGS = new ArrayList<>();

    public static final KeyMapping KEY_PACO = PaCoKeyMappings.register(new KeyMapping(
            "key.pandora_core.pandora_core",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            "category.pandora_core.pandora_core"
    ));

    public static KeyMapping register(KeyMapping keyMapping) {
        KEY_MAPPINGS.add(keyMapping);
        return keyMapping;
    }
}