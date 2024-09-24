package com.github.andrew0030.pandora_core.utils.data_holders;

import com.github.andrew0030.pandora_core.utils.update_checker.UpdateChecker;
import org.jetbrains.annotations.ApiStatus;

import java.net.URL;
import java.util.List;
import java.util.Optional;

/** Holder class to store mod data. */
public abstract class ModDataHolder {
    private Optional<UpdateChecker.Status> status = Optional.empty();

    /** @return The mod id. */
    public abstract String getModId();

    /** @return The mod name. */
    public abstract String getModName();

    /** @return The mod version. */
    public abstract String getModVersion();

    /** @return The mod icon name. */
    public abstract List<String> getModIconFiles();

    /** @return Whether scaling should blur the mod icon. */
    public abstract Optional<Boolean> getBlurModIcon();

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
    public abstract boolean isOutdated();
}