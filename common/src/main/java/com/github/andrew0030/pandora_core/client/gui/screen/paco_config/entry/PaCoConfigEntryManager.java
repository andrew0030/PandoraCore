package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.BaseConfigEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class PaCoConfigEntryManager {
    private static final Map<Class<? extends BaseConfigEntry>, ConfigEntryFactory> FACTORY_CACHE = new HashMap<>();

    /**
     * Retrieves a lazily cached {@link ConfigEntryFactory} for the specified {@link BaseConfigEntry} class.
     *
     * @param entryClass The class of the {@link BaseConfigEntry} to create a {@link ConfigEntryFactory} for
     * @return The cached {@link ConfigEntryFactory} used to instantiate the {@link BaseConfigEntry}
     * @throws RuntimeException If the required constructor is missing or instantiation fails
     */
    public static ConfigEntryFactory getFactory(Class<? extends BaseConfigEntry> entryClass) {
        return FACTORY_CACHE.computeIfAbsent(entryClass, clazz -> {
            try {

                // TODO maybe adjust the values passed to BaseConfigEntry
                Constructor<? extends BaseConfigEntry> constructor = clazz.getConstructor(
                        PaCoConfigScreen.class, ConfigTreeNode.class,
                        int.class, int.class, int.class, int.class
                );

                // Lambda captures the retrieved constructor, allowing for subsequent calls to use the same constructor instance
                return (screen, node, x, y, w, h) -> {
                    try {
                        return constructor.newInstance(screen, node, x, y, w, h);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate " + clazz.getName(), e);
                    }
                };
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Missing constructor for " + clazz.getName(), e);
            }
        });
    }
}