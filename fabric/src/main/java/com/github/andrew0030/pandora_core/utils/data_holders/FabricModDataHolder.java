package com.github.andrew0030.pandora_core.utils.data_holders;

import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FabricModDataHolder extends ModDataHolder {
    private final ModMetadata metadata;
    private final List<String> icons = new ArrayList<>();
    private Optional<Boolean> blurIcon = Optional.empty();

    public FabricModDataHolder(ModMetadata metadata) {
        this.metadata = metadata;
        /* We check for all valid mod icons and add them to the list */
        // Pandora Core
        Optional.ofNullable(metadata.getCustomValue("pandoracore"))
                .filter(val -> val.getType() == CustomValue.CvType.OBJECT)
                .map(CustomValue::getAsObject)
                .ifPresent(pandoracoreObj -> {
                    // Icon
                    Optional.ofNullable(pandoracoreObj.get("icon"))
                            .filter(iconVal -> iconVal.getType() == CustomValue.CvType.STRING)
                            .map(CustomValue::getAsString)
                            .ifPresent(this.icons::add);
                    // Blur Icon
                    Optional.ofNullable(pandoracoreObj.get("blurIcon"))
                            .filter(blurIconVal -> blurIconVal.getType() == CustomValue.CvType.BOOLEAN)
                            .map(CustomValue::getAsBoolean)
                            .ifPresent(val -> this.blurIcon = Optional.of(val));
                });
        // Catalogue
        Optional.ofNullable(metadata.getCustomValue("catalogue"))
                .filter(val -> val.getType() == CustomValue.CvType.OBJECT)
                .map(CustomValue::getAsObject)
                .map(catalogueObj -> catalogueObj.get("icon"))
                .filter(iconVal -> iconVal.getType() == CustomValue.CvType.OBJECT)
                .map(CustomValue::getAsObject)
                .map(iconObj -> iconObj.get("image"))
                .filter(imageVal -> imageVal.getType() == CustomValue.CvType.STRING)
                .map(CustomValue::getAsString)
                .ifPresent(this.icons::add);
        // Fabric
        metadata.getIconPath(0).ifPresent(this.icons::add);
    }

    @Override
    public String getModId() {
        return this.metadata.getId();
    }

    @Override
    public String getModName() {
        return this.metadata.getName();
    }

    @Override
    public String getModVersion() {
        return this.metadata.getVersion().getFriendlyString();
    }

    @Override
    public List<String> getModIconFiles() {
        return this.icons;
    }

    @Override
    public Optional<Boolean> getBlurModIcon() {
        return this.blurIcon;
    }
}