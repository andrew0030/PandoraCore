package com.github.andrew0030.pandora_core.utils;

import javax.annotation.Nullable;

/** Holder class to store mod data, for mods managed by PaCo. */
public class ModDataHolder {
    private final String modId;
    private String modName;
    private String modVersion;
    private String modIconFile;

    private ModDataHolder(String modId) {
        this.modId = modId;
    }

    public static ModDataHolder forMod(String modId) {
        return new ModDataHolder(modId);
    }

    public void setModName(String modName) {
        this.modName = modName;
    }

    public void setModVersion(String modVersion) {
        this.modVersion = modVersion;
    }

    public void setModIconFile(String modIconFile) {
        this.modIconFile = modIconFile;
    }

    public String getModId() {
        return this.modId;
    }

    @Nullable
    public String getModName() {
        return this.modName;
    }

    /** @return The Mod name, or the ID if the name is null. */
    public String getModNameOrId() {
        return this.modName != null ? this.modName : this.modId;
    }

    @Nullable
    public String getModVersion() {
        return this.modVersion;
    }

    @Nullable
    public String getModIconFile() {
        return this.modIconFile;
    }
}