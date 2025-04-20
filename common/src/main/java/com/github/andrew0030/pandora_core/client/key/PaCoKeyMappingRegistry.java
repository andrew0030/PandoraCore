package com.github.andrew0030.pandora_core.client.key;

import com.github.andrew0030.pandora_core.platform.Services;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple, cross-platform registry helper used for registering KeyMappings, in a unified way for all mod loaders.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * public static final PaCoKeyMappingRegistry KEY_MAPPINGS = new PaCoKeyMappingRegistry();
 * public static final KeyMapping KEY_EXAMPLE = KEY_MAPPINGS.register(
 *     new KeyMapping(
 *         "key.example_mod.example_key",
 *         InputConstants.Type.KEYSYM,
 *         GLFW.GLFW_KEY_F,
 *         "category.example_mod.example_key"
 *     ), () -> {
 *         Minecraft.getInstance().setScreen(new ExampleScreen());
 *     }
 * );
 * }</pre>
 * <p>And then client side during mod construction:</p>
 * <pre>{@code
 * ExampleModKeyMappings.KEY_MAPPINGS.register();
 * }</pre>
 */
public class PaCoKeyMappingRegistry {
    private final List<Pair<KeyMapping, Runnable>> KEY_MAPPINGS = new ArrayList<>();

    /**
     * Adds a new {@link KeyMapping} and {@link Runnable}, to be registered later.
     * @param keyMapping The {@link KeyMapping} to be registered.
     * @param runnable   The {@link Runnable} that will run when the key is pressed.
     * @return The given {@link KeyMapping}, for convenient assignment.
     */
    public KeyMapping add(KeyMapping keyMapping, Runnable runnable) {
        KEY_MAPPINGS.add(new Pair<>(keyMapping, runnable));
        return keyMapping;
    }

    /**
     * This needs to be called, so event listeners are created by the loaders.<br/>
     * Here is a list of when to call it, on each loader:<br/><br/>
     * <strong>Forge</strong>: Client side, inside mod constructor.<br/>
     * <strong>Fabric</strong>: Inside ClientModInitializer#onInitializeClient.<br/>
     */
    public void register() {
        Services.REGISTRY.registerKeyMappings(KEY_MAPPINGS);
    }
}