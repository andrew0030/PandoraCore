package com.github.andrew0030.pandora_core.platform.services;

import com.github.andrew0030.pandora_core.utils.ModDataHolder;
import com.mojang.blaze3d.platform.NativeImage;

import java.util.Optional;
import java.util.function.Consumer;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    void loadNativeImage(String modId, String resource, Consumer<NativeImage> consumer);

    /**
     * Creates a {@link ModDataHolder} for the Mod with the given ID.
     * @param modId The ID of the Mod that will be looked for.
     */
    ModDataHolder getModDataHolder(String modId);
}