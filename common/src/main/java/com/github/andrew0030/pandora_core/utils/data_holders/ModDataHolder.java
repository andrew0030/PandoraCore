package com.github.andrew0030.pandora_core.utils.data_holders;

import com.github.andrew0030.pandora_core.utils.mod_warnings.ModWarningProvider;
import com.github.andrew0030.pandora_core.utils.update_checker.UpdateChecker;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/** Holder class to store mod data. */
public abstract class ModDataHolder {
    protected static final List<Component> NO_WARNINGS = new ArrayList<>();
    private Optional<UpdateChecker.Status> status = Optional.empty();

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
    public abstract List<String> getModIconFiles();

    /** @return Whether scaling should blur the mod icon. */
    public abstract Optional<Boolean> getBlurModIcon();

    /** @return A list of found mod background names. */
    public abstract List<String> getModBackgroundFiles();

    /** @return An {@link URL} pointing to an update JSON file, used to check for updates. */
    public abstract Optional<URL> getUpdateURL();

    /**
     * @return An {@link Optional} containing the {@link UpdateChecker.Status} of this mod.
     * If no update URL is provided this will return an empty {@link Optional}.
     * */
    public Optional<UpdateChecker.Status> getUpdateStatus() {
        return this.status;
    }

    /** Used internally to update the "update status" of a mod. */
    @ApiStatus.Internal
    public void setUpdateStatus(UpdateChecker.Status status) {
        this.status = Optional.ofNullable(status);
    }

    /** @return Whether this mod is outdated, if no update URL is provided this defaults to false. */
    public boolean isOutdated() {
        return this.getUpdateStatus().map(UpdateChecker.Status::isOutdated).orElse(false);
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
            // Ensures the class implements ModWarningProvider
            if (!ModWarningProvider.class.isAssignableFrom(factoryClass))
                throw new RuntimeException(className + " does not implement ModWarningProvider.");
            // Makes sure there is a no-argument constructor
            try {
                factoryClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Class " + className + " must have a no-argument constructor.", e);
            }
            // Instantiates the class
            ModWarningProvider provider = (ModWarningProvider) factoryClass.getDeclaredConstructor().newInstance();
            return provider.getWarnings();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Warning factory class not found: " + className, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate or invoke warning factory: " + className, e);
        }
    }

    @Nullable
    public abstract String getSha512Hash();
}