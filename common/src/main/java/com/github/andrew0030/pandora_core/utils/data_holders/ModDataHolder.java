package com.github.andrew0030.pandora_core.utils.data_holders;

import com.github.andrew0030.pandora_core.utils.update_checker.UpdateChecker;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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

    //TODO descriptions
    public abstract Optional<URL> getUpdateURL();
    public Optional<UpdateChecker.Status> getUpdateStatus() {
        return this.status;
    }

    @ApiStatus.Internal
    public void setUpdateStatus(UpdateChecker.Status status) {
        this.status = Optional.ofNullable(status);
    }
}