package com.github.andrew0030.pandora_core.utils.data_holders;

import com.github.andrew0030.pandora_core.utils.update_checker.UpdateChecker;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.network.chat.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FabricModDataHolder extends ModDataHolder {
    private final ModMetadata metadata;
    private final List<String> icons = new ArrayList<>();
    private Optional<Boolean> blurIcon = Optional.empty();
    private final List<String> backgrounds = new ArrayList<>();
    private Optional<URL> updateURL = Optional.empty();
    private final List<Component> modWarnings = new ArrayList<>();

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
                    // Background
                    Optional.ofNullable(pandoracoreObj.get("background"))
                            .filter(backgroundVal -> backgroundVal.getType() == CustomValue.CvType.STRING)
                            .map(CustomValue::getAsString)
                            .ifPresent(this.backgrounds::add);
                    // Mod Warnings
                    Optional.ofNullable(pandoracoreObj.get("warningFactory"))
                            .filter(factoryVal -> factoryVal.getType() == CustomValue.CvType.STRING)
                            .map(CustomValue::getAsString)
                            .ifPresent(factoryClass -> {
                                this.modWarnings.addAll(this.loadWarningsFromFactory(factoryClass));
                            });
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
                .ifPresent(catalogueObj -> {
                    // Icon
                    Optional.ofNullable(catalogueObj.get("icon"))
                            .filter(iconVal -> iconVal.getType() == CustomValue.CvType.OBJECT)
                            .map(CustomValue::getAsObject)
                            .map(iconObj -> iconObj.get("image"))
                            .filter(imageVal -> imageVal.getType() == CustomValue.CvType.STRING)
                            .map(CustomValue::getAsString)
                            .ifPresent(this.icons::add);
                    // Background
                    Optional.ofNullable(catalogueObj.get("background"))
                            .filter(backgroundVal -> backgroundVal.getType() == CustomValue.CvType.STRING)
                            .map(CustomValue::getAsString)
                            .ifPresent(this.backgrounds::add);
                });
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
    public String getModDescription() {
        return this.metadata.getDescription();
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
    public List<String> getModBackgroundFiles() {
        return this.backgrounds;
    }

    @Override
    public Optional<URL> getUpdateURL() {
        return this.updateURL;
    }

    @Override
    public List<Component> getModWarnings() {
        return this.modWarnings;
    }
}