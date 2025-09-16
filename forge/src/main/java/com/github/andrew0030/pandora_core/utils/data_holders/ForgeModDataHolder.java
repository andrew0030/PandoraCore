package com.github.andrew0030.pandora_core.utils.data_holders;

import com.github.andrew0030.pandora_core.platform.Services;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

public class ForgeModDataHolder extends ModDataHolder {
    private final IModInfo modInfo;
    private final List<String> icons = new ArrayList<>();
    private Optional<Boolean> blurIcon = Optional.empty();
    private final List<String> backgrounds = new ArrayList<>();
    private final List<String> banners = new ArrayList<>();
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
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreBanner"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(this.banners::add);
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
        modInfo.getLogoFile().ifPresent(logo -> {
            this.icons.add(logo);   // Used to render mod icon
            this.banners.add(logo); // Used to render mod banner
        });
        modInfo.getUpdateURL().ifPresent(url -> this.updateURL = this.updateURL.isPresent() ? this.updateURL : modInfo.getUpdateURL());

        ((ModInfo) modInfo).getConfigElement("authors").map(Object::toString).ifPresent(string -> this.authors.addAll(Arrays.stream(string.replaceAll(" (and|&) ", ",").split("(?<=:)|[,;]")).map(String::trim).filter(s -> !s.isEmpty()).toList()));
        ((ModInfo) modInfo).getConfigElement("credits").map(Object::toString).ifPresent(string -> this.credits.addAll(Arrays.stream(string.replaceAll("^[\\r\\n]+|[\\r\\n]+$", "").split("\\n")).map(String::trim).filter(s -> !s.isEmpty()).toList()));
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
    public List<String> getModBannerFiles() {
        return this.banners;
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

    @Override
    public Optional<String> getSha512Hash() {

        // TODO: remove this
        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            File libsDir = new File(Services.PLATFORM.getGameDirectory().toString().replaceAll("run", "libs"));
            if (libsDir.exists() && libsDir.isDirectory()) {
                File[] files = libsDir.listFiles();
                if (files != null) {
                    for (File libFile : files) {
                        if (libFile.getName().contains(modInfo.getModId())) {
                            try {
                                return Optional.of(Files.asByteSource(libFile).hash(Hashing.sha512()).toString());
                            } catch (IOException e) {
                                return Optional.empty();
                            }
                        }
                    }
                }
            }
            return Optional.empty();
        }

        File file = this.modInfo.getOwningFile().getFile().getFilePath().toFile();
        // We make sure the file is a .jar and not a folder
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".jar")) return Optional.empty();
        // We make sure the file is a valid file
        if (!file.isFile()) return Optional.empty();

        // If the file contains multiple mods (it embeds mods), we compare the id of the current mod to
        // the first entry. If the ids don't match the current mod is most likely embedded, so we skip it.
        List<IModInfo> modsInFile = this.modInfo.getOwningFile().getMods();
        if (modsInFile.size() > 1 && !modsInFile.get(0).getModId().equals(this.modInfo.getModId()))
            return Optional.empty();

        // Lastly we read the file and hash it using SHA512
        try {
            return Optional.of(Files.asByteSource(file).hash(Hashing.sha512()).toString());
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}