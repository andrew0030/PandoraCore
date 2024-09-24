package com.github.andrew0030.pandora_core.utils.data_holders;

import com.github.andrew0030.pandora_core.utils.update_checker.UpdateChecker;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FabricModDataHolder extends ModDataHolder {
    private final ModMetadata metadata;
    private final List<String> icons = new ArrayList<>();
    private Optional<Boolean> blurIcon = Optional.empty();
    private Optional<URL> updateURL = Optional.empty();

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
                    // Because its handy I also check for all other paco properties here
                    // Update URL
                    Optional.ofNullable(pandoracoreObj.get("updateURL"))
                            .filter(updateURLVal -> updateURLVal.getType() == CustomValue.CvType.STRING)
                            .map(CustomValue::getAsString)
                            .ifPresent(val -> {
                                if (val.trim().isEmpty() || val.contains("myurl.me") || val.contains("example.invalid"))
                                    this.updateURL = Optional.empty();
                                try {
                                    this.updateURL = Optional.of(new URL(val)); }
                                catch (MalformedURLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
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

        // TODO add config option to disable update checking
        UpdateChecker.checkForUpdate(this);
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

    @Override
    public Optional<URL> getUpdateURL() {
        return this.updateURL;
    }

    @Override
    public boolean isOutdated() {
        return this.getUpdateStatus().map(UpdateChecker.Status::isOutdated).orElse(false);
    }
}