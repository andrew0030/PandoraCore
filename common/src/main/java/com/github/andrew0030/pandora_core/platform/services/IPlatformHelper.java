package com.github.andrew0030.pandora_core.platform.services;

import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.mojang.blaze3d.platform.NativeImage;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

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

    /**
     * Gets the current game version as a {@link String}.
     * @return The current game version.
     */
    String getMinecraftVersion();

    /**
     * Gets the game's directory {@link Path}.
     * @return A {@link Path} pointing to the game's directory.
     */
    Path getGameDirectory();

    /**
     * Gets the game's config directory {@link Path}.
     * @return A {@link Path} pointing to the game's config directory.
     */
    Path getConfigDirectory();

    <T> T loadNativeImage(String modId, String resource, Function<NativeImage, T> consumer);

    /** Creates a {@link List} with a {@link ModDataHolder} for each loaded mod. */
    List<ModDataHolder> getModDataHolders();
}