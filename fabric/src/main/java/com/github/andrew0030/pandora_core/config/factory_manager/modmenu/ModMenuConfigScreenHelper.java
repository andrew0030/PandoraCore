package com.github.andrew0030.pandora_core.config.factory_manager.modmenu;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class ModMenuConfigScreenHelper {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "ModMenuConfigScreenHelper");
    static final Map<String, String> FACTORY_PATHS = new HashMap<>();
    private static final ModMenuStrategy STRATEGY;

    static {
        // Extracts and caches all the modmenu factory paths, without invoking the entry points
        FabricLoader.getInstance().getAllMods().forEach(container -> {
            String modId = container.getMetadata().getId();
            ModMenuConfigScreenHelper.getModMenuEntrypoint(container).ifPresent(path -> FACTORY_PATHS.put(modId, path));
        });
        // Determines how to load modmenu config factories, based on whether its installed or not.
        // The reason we don't use fabric for this, is because there is other mods that ship with
        // dummy classes that replicate modmenu. For our purposes these also work, so we use them.
        ModMenuStrategy strategy;
        try {
            Class.forName("com.terraformersmc.modmenu.api.ModMenuApi");
            strategy = new ModMenuInstalledStrategy();
        } catch (ClassNotFoundException e) {
            strategy = new ModMenuMissingStrategy();
        }
        STRATEGY = strategy;
    }

    /**
     * Returns a {@link BiFunction} that invokes the {@code getModConfigScreenFactory} method,
     * or {@link Optional#empty()} if the {@code class}/{@code method} doesn't exist.
     */
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static Optional<BiFunction<Screen, ModContainer, Screen>> getScreenFactory(String modId) {
        return STRATEGY.getScreenFactory(modId);
    }

    /**
     * Returns a {@link Map} of {@code modId} -> {@link BiFunction}, which invokes
     * the {@code getProvidedConfigScreenFactories} method, or {@link Optional#empty()}
     * if the {@code class}/{@code method} doesn't exist.
     */
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static Optional<Map<String, BiFunction<Screen, ModContainer, Screen>>> getScreenFactoryProvider(String modId) {
        return STRATEGY.getScreenFactoryProvider(modId);
    }

    /**
     * Attempts to manually extract the "path" String, stored within the "modmenu" entry point, (in order to avoid class-loading).
     *
     * @param container The {@link ModContainer} that will have its "path" extracted
     * @return If a "path" was found an {@link Optional} containing the path as a {@code String}, otherwise {@link Optional#empty()}
     */
    private static Optional<String> getModMenuEntrypoint(ModContainer container) {
        Optional<Path> pathOpt = container.findPath("fabric.mod.json");
        // If we can't find the path to the fabric.mod.json there is no point in any further logic...
        if (pathOpt.isEmpty()) return Optional.empty();
        // Loads the file as a JsonObject
        try (Reader reader = Files.newBufferedReader(pathOpt.get(), StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonObject()) return Optional.empty();
            JsonObject json = root.getAsJsonObject();
            // If "entrypoints" exists in the json (should always be the case) we grab them
            JsonElement entrypointsEl = json.get("entrypoints");
            if (entrypointsEl == null || !entrypointsEl.isJsonObject()) return Optional.empty();
            JsonObject entrypoints = entrypointsEl.getAsJsonObject();
            // If there is no "modmenu" entry in the "entrypoints" we exit early, otherwise we retrieve it
            JsonElement modmenuEl = entrypoints.get("modmenu");
            if (modmenuEl == null || !modmenuEl.isJsonArray()) return Optional.empty();
            JsonArray array = modmenuEl.getAsJsonArray();

            // Tries to load all entry points until a valid one is found, or there are no more to check
            for (JsonElement el : array) {
                // Plain String
                if (el.isJsonPrimitive()) {
                    String value = el.getAsString();
                    if (ModMenuConfigScreenHelper.isValidClassName(value)) return Optional.of(value);
                    continue;
                }
                // Object with optional adapter
                if (el.isJsonObject()) {
                    JsonObject obj = el.getAsJsonObject();
                    // Skips unsupported adapters
                    JsonElement adapterEl = obj.get("adapter");
                    if (adapterEl != null && adapterEl.isJsonPrimitive()) {
                        String adapter = adapterEl.getAsString();
                        if (!"fabric".equals(adapter) && !"default".equals(adapter)) continue;
                    }
                    // Extracts and validates the class name for entries that haven't been skipped
                    JsonElement valueEl = obj.get("value");
                    if (valueEl != null && valueEl.isJsonPrimitive()) {
                        String value = valueEl.getAsString();
                        if (ModMenuConfigScreenHelper.isValidClassName(value)) return Optional.of(value);
                    }
                }
            }
        } catch (IOException ignored) {
            LOGGER.error("Failed to read fabric.mod.json for: '{}'", container.getMetadata().getId());
        } catch (JsonParseException ignored) {
            LOGGER.error("Could not parse fabric.mod.json for: '{}'", container.getMetadata().getId());
        }
        // If path retrieval fails we simply return an empty optional
        return Optional.empty();
    }

    /**
     * @param path The class path to check
     * @return Whether the class path is a valid one
     */
    private static boolean isValidClassName(String path) {
        if (StringUtil.isNullOrEmpty(path)) return false;
        // We reject method references and invalid characters
        if (path.contains("::") || path.contains(" ") || path.contains("(")) return false;
        // Haven't tested if inner (package.Class$Inner) classes work, but with this they should flag as valid
        return path.matches("^[a-zA-Z_$][\\w$]*(\\.[a-zA-Z_$][\\w$]*)*$");
    }
}