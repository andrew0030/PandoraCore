package com.github.andrew0030.pandora_core.utils.data_holders;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Holder class to store mod data. */
public abstract class ModDataHolder {

    /** @return The mod id. */
    public abstract String getModId();

    /** @return The mod name. */
    public abstract String getModName();

    /** @return The mod version. */
    public abstract String getModVersion();

    /** @return The mod icon name. */
    public abstract List<String> getModIconFiles();

    /** @return Whether scaling should blur the mod icon. */
    @Nullable
    public abstract Boolean getBlurModIcon();
}