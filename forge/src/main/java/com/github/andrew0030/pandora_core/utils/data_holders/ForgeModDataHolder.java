package com.github.andrew0030.pandora_core.utils.data_holders;

import com.github.andrew0030.pandora_core.utils.update_checker.UpdateChecker;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.forgespi.language.IModInfo;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForgeModDataHolder extends ModDataHolder {
    private final IModInfo modInfo;
    private final List<String> icons = new ArrayList<>();
    private Optional<Boolean> blurIcon = Optional.empty();
    private Optional<URL> updateURL = Optional.empty();

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
                .ifPresent(val -> this.blurIcon = Optional.of(val));
        // Catalogue
        Optional.ofNullable(this.modInfo.getModProperties().get("catalogueImageIcon"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(this.icons::add);
        // Forge
        modInfo.getLogoFile().ifPresent(this.icons::add);

        /* We check if there is an update checking URL, and perform a check if needed */
        // Pandora Core
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreUpdateURL"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(val -> this.updateURL = Optional.ofNullable(StringUtils.toURL(val)));
        // Forge
        modInfo.getUpdateURL().ifPresent(url -> this.updateURL = this.updateURL.isPresent() ? this.updateURL : modInfo.getUpdateURL());

        // TODO add config option to disable update checking
        UpdateChecker.checkForUpdate(this);
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
    public Optional<Boolean> getBlurModIcon() {
        return this.blurIcon;
    }

    @Override
    public Optional<URL> getUpdateURL() {
        return this.updateURL;
    }
}