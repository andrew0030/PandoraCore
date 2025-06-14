package com.github.andrew0030.pandora_core.utils.data_holders;

import com.github.andrew0030.pandora_core.utils.update_checker.UpdateChecker;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ForgeModDataHolder extends ModDataHolder {
    private final IModInfo modInfo;
    private final List<String> icons = new ArrayList<>();
    private Optional<Boolean> blurIcon = Optional.empty();
    private final List<String> backgrounds = new ArrayList<>();
    private Optional<URL> updateURL = Optional.empty();
    private Supplier<List<Component>> modWarnings;
    private final List<String> authors = new ArrayList<>();
    private final List<String> credits = new ArrayList<>();

    public ForgeModDataHolder(IModInfo modInfo) {
        this.modInfo = modInfo;
        // Pandora Core
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreIcon"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(this.icons::add);
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreBlurIcon"))
                .filter(Boolean.class::isInstance)
                .map(Boolean.class::cast)
                .ifPresent(val -> this.blurIcon = Optional.of(val));
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreBackground"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(this.backgrounds::add);
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreUpdateURL"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(val -> this.updateURL = Optional.ofNullable(StringUtils.toURL(val)));
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreWarningFactory"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(factoryClass -> {
                    this.modWarnings = this.loadWarningsFromFactory(factoryClass);
                });
        // Catalogue
        Optional.ofNullable(this.modInfo.getModProperties().get("catalogueImageIcon"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(this.icons::add);
        Optional.ofNullable(this.modInfo.getModProperties().get("catalogueBackground"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(this.backgrounds::add);
        // Forge
        modInfo.getLogoFile().ifPresent(this.icons::add);
        modInfo.getUpdateURL().ifPresent(url -> this.updateURL = this.updateURL.isPresent() ? this.updateURL : modInfo.getUpdateURL());

        ((ModInfo) modInfo).getConfigElement("authors").map(Object::toString).ifPresent(string -> this.authors.addAll(Arrays.stream(string.replaceAll(" (and|&) ", ",").split("(?<=:)|[,;]")).map(String::trim).filter(s -> !s.isEmpty()).toList()));
        ((ModInfo) modInfo).getConfigElement("credits").map(Object::toString).ifPresent(string -> this.credits.addAll(Arrays.stream(string.replaceAll("^[\\r\\n]+|[\\r\\n]+$", "").split("\\n")).map(String::trim).filter(s -> !s.isEmpty()).toList()));

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
    public String getModDescription() {
        // Trims new line characters from the description, as we don't want it to affect the of the PaCo screen.
        return this.modInfo.getDescription().replaceAll("^[\\r\\n]+|[\\r\\n]+$", "");
    }

    @Override
    public List<String> getModAuthors() {
        return this.authors;
    }

    @Override
    public List<String> getModCredits() {
        return this.credits;
    }

    @Override
    public String getModLicense() {
        return this.modInfo.getOwningFile().getLicense();
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
    public List<String> getModBackgroundFiles() {
        return this.backgrounds;
    }

    @Override
    public Optional<URL> getUpdateURL() {
        return this.updateURL;
    }

    @Override
    public List<Component> getModWarnings() {
        if (this.modWarnings == null) return NO_WARNINGS;
        return this.modWarnings.get();
    }
}