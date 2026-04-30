package com.github.andrew0030.pandora_core.utils.data_holders;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.github.andrew0030.pandora_core.utils.update_checker.UpdateInfo;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/** Holder class to store mod data. */
public abstract class ModDataHolder {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "ModDataHolder");
    protected static final List<Component> NO_WARNINGS = new ArrayList<>();
    private Optional<UpdateInfo> info = Optional.empty();

    /** @return The mod id. */
    public abstract String getModId();

    /** @return The mod name. */
    public abstract String getModName();

    /** @return The mod description. */
    public abstract String getModDescription();

    /** @return The mod authors. */
    public abstract List<String> getModAuthors();

    /** @return Whether this mod has any mod authors specified. */
    public boolean hasModAuthors() {
        return !this.getModAuthors().isEmpty();
    }

    /** @return The mod credits/contributors. */
    public abstract List<String> getModCredits();

    /** @return Whether this mod has any mod credits/contributors specified. */
    public boolean hasModCredits() {
        return !this.getModCredits().isEmpty();
    }

    /** @return The mod license. */
    public abstract String getModLicense();

    /** @return The mod version. */
    public abstract String getModVersion();

    /** @return A list of found mod icon names. */
    public abstract List<Pair<String, String>> getModIconFiles();

    /** @return A list of found mod background names. */
    public abstract List<Pair<String, String>> getModBackgroundFiles();

    /** @return A list of found mod banner names. */
    public abstract List<Pair<String, String>> getModBannerFiles();

    /** @return Whether scaling should blur the mod icon. */
    public abstract Optional<Boolean> getBlurModIcon();

    /** @return Whether scaling should blur the mod background. */
    public abstract Optional<Boolean> getBlurModBackground();

    /** @return Whether scaling should blur the mod banner. */
    public abstract Optional<Boolean> getBlurModBanner();

    /** @return An {@link URL} pointing to an update JSON file, used to check for updates. */
    public abstract Optional<URL> getUpdateURL();

    /** @return A {@link Map} with its {@code keys} used for context (e.g. homepage, issues) and their corresponding {@link URL}. */
    public abstract Map<String, URL> getContactURLs();

    /**
     * @return An {@link Optional} containing the {@link UpdateInfo} of this mod.
     * If no update URL is provided this will return an empty {@link Optional}.
     */
    public Optional<UpdateInfo> getUpdateInfo() {
        return this.info;
    }

    /** Used internally to update the {@link UpdateInfo} of a mod. */
    @ApiStatus.Internal
    public void setUpdateInfo(UpdateInfo info) {
        this.info = Optional.ofNullable(info);
    }

    /** @return Whether this mod is outdated, if no update URL is provided this defaults to false. */
    public boolean isOutdated() {
        if (this.getUpdateInfo().isEmpty()) return false;
        return this.getUpdateInfo().get().getStatus().isOutdated();
    }

    public abstract List<Component> getModWarnings();

    /** @return Whether this mod has any warnings. */
    public boolean hasModWarnings() {
        return !this.getModWarnings().isEmpty();
    }

    @ApiStatus.Internal
    protected Supplier<List<Component>> loadWarningsFromFactory(String className) {
        try {
            Class<?> factoryClass = Class.forName(className);
            // The expected signature: () -> List<Component>
            MethodType mt = MethodType.methodType(List.class);
            MethodHandle handle;
            try {
                handle = MethodHandles.publicLookup().findStatic(factoryClass, "getWarningFactory", mt);
            } catch (NoSuchMethodException | IllegalAccessException e) {
                LOGGER.error("Failed to locate 'getWarningFactory' in warning factory: {}", className, e);
                return null;
            }
            // Lambda captures the method handle
            return () -> {
                try {
                    return (List<Component>) handle.invoke();
                } catch (Throwable t) {
                    LOGGER.error("Failed to invoke warning factory: {}", className, t);
                    return List.of();
                }
            };
        } catch (ClassNotFoundException e) {
            LOGGER.error("Warning factory class not found: {}", className, e);
            return null;
        }
    }

    public abstract Optional<String> getSha512Hash();

    public abstract Optional<Screen> getConfigScreen(Minecraft mc, Screen current);

    /**
     * Converts the given {@link String} to an {@link URL}.
     *
     * @param string The {@link String} that will be converted
     * @return The @return {@link URL} retrieved from the given {@code string}.
     */
    protected Optional<URL> toURL(String string) {
        if (string != null && !string.trim().isEmpty() && !string.contains("myurl.me") && !string.contains("example.invalid")) {
            try {
                URL url = URI.create(string).toURL();
                return Optional.of(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }
}