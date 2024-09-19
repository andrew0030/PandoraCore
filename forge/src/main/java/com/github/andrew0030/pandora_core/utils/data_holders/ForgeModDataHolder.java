package com.github.andrew0030.pandora_core.utils.data_holders;

import net.minecraftforge.forgespi.language.IModInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForgeModDataHolder extends ModDataHolder {
    private final IModInfo modInfo;
    private final List<String> icons = new ArrayList<>();
    private Boolean blurIcon;

    public ForgeModDataHolder(IModInfo modInfo) {
        this.modInfo = modInfo;
        /* We check for all valid mod icons and add them to the list */
        // Pandora Core
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreIcon"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(this.icons::add);
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreBlurIcon"))
                .filter(Boolean.class::isInstance)
                .map(Boolean.class::cast)
                .ifPresent(val -> this.blurIcon = val);
        // Catalogue
        Optional.ofNullable(this.modInfo.getModProperties().get("catalogueImageIcon"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(this.icons::add);
        // Forge
        modInfo.getLogoFile().ifPresent(this.icons::add);
    }

    @Override
    public String getModId() {
        return this.modInfo.getModId();
    }

    @Override
    public String getModName() {
        return this.modInfo.getDisplayName();
    }

    @Override
    public String getModVersion() {
        return this.modInfo.getVersion().toString();
    }

    @Override
    public List<String> getModIconFiles() {
        return this.icons;
    }

    @Override
    public Boolean getBlurModIcon() {
        return this.blurIcon;
    }
}