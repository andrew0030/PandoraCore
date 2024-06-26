package com.github.andrew0030.pandora_core.client.key;

import com.github.andrew0030.pandora_core.platform.Services;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;

/** Helper Class that allows registering KeyMappings. */
public class PaCoKeyMappingRegistry {
    private final List<Pair<KeyMapping, Runnable>> KEY_MAPPINGS = new ArrayList<>();

    /**
     * Registers a new {@link KeyMapping} and a {@link Runnable}, which will be run when the key is pressed.
     * @param keyMapping The {@link KeyMapping} to be registered.
     * @param runnable The {@link Runnable} that will run when the key is pressed.
     * @return The given {@link KeyMapping}.
     */
    public KeyMapping register(KeyMapping keyMapping, Runnable runnable) {
        KEY_MAPPINGS.add(new Pair<>(keyMapping, runnable));
        return keyMapping;
    }

    /**
     * This needs to be called on each loader, so event listeners are created for said loader.<br/>
     * It is important to note that this needs to be called pretty early on certain loaders.<br/>
     * Here is a list of when to call it for each loader:<br/><br/>
     * <strong>Forge</strong>: Inside Client Init (NOTE: <strong>not</strong> FMLClientSetupEvent, See <strong>PandoraCoreClientForge</strong>).<br/>
     * <strong>Fabric</strong>: Inside ClientModInitializer#onInitializeClient (See <strong>PandoraCoreClientFabric</strong>).<br/>
     */
    public void registerKeyBindings() {
        Services.KEY_MAPPING.registerKeyMappings(KEY_MAPPINGS);
    }
}