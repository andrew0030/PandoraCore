package com.github.andrew0030.pandora_core.utils.data_holders;

import com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection.ModButton;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements.BackgroundContentElement;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements.BannerContentElement;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.tuple.Triple;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    private final List<Pair<String, String>> icons = new ArrayList<>();
    private final List<Pair<String, String>> backgrounds = new ArrayList<>();
    private final List<Pair<String, String>> banners = new ArrayList<>();
    // Note: the reason we store values in optionals, is to avoid
    // creating a new instance every time the getters are called.
    private Optional<Boolean> blurIcon = Optional.empty();
    private Optional<Boolean> blurBackground = Optional.empty();
    private Optional<Boolean> blurBanner = Optional.empty();
    private Optional<URL> updateURL = Optional.empty();
    private Supplier<List<Component>> modWarnings;
    private final List<String> authors = new ArrayList<>();
    private final List<String> credits = new ArrayList<>();

    public ForgeModDataHolder(IModInfo modInfo) {
        this.modInfo = modInfo;
        // [######| Pandora Core |######]
        // Icon
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreIcon"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(ResourceLocation::isValidPath) // Skips entry if someone used a weird char, as we only want paths
                .ifPresent(val -> this.icons.add(Pair.of(this.getModId(), val)));
        // Background
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreBackground"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(ResourceLocation::isValidPath) // Skips entry if someone used a weird char, as we only want paths
                .ifPresent(val -> this.backgrounds.add(Pair.of(this.getModId(), val)));
        // Banner
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreBanner"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(ResourceLocation::isValidPath) // Skips entry if someone used a weird char, as we only want paths
                .ifPresent(val -> this.banners.add(Pair.of(this.getModId(), val)));
        // Blur Icon
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreBlurIcon"))
                .filter(Boolean.class::isInstance)
                .map(Boolean.class::cast)
                .ifPresent(val -> this.blurIcon = Optional.of(val));
        // Blur Background
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreBlurBackground"))
                .filter(Boolean.class::isInstance)
                .map(Boolean.class::cast)
                .ifPresent(val -> this.blurBackground = Optional.of(val));
        // Blur Banner
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreBlurBanner"))
                .filter(Boolean.class::isInstance)
                .map(Boolean.class::cast)
                .ifPresent(val -> this.blurBanner = Optional.of(val));
        // Update URL
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreUpdateURL"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(val -> this.updateURL = Optional.ofNullable(StringUtils.toURL(val)));
        // Mod Warnings
        Optional.ofNullable(this.modInfo.getModProperties().get("pandoracoreWarningFactory"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(factoryClass -> {
                    this.modWarnings = this.loadWarningsFromFactory(factoryClass);
                });

        // [######| Catalogue |######]
        // Icon
        Optional.ofNullable(this.modInfo.getModProperties().get("catalogueImageIcon"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(ResourceLocation::isValidPath) // Skips entry if someone used a weird char, as we only want paths
                .ifPresent(val -> this.icons.add(Pair.of(this.getModId(), val)));
        // Background
        Optional.ofNullable(this.modInfo.getModProperties().get("catalogueBackground"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(ResourceLocation::isValidPath) // Skips entry if someone used a weird char, as we only want paths
                .ifPresent(val -> this.backgrounds.add(Pair.of(this.getModId(), val)));

        // [######| Internal Fallback Textures |######]
        // Added before Forge, as we want to prioritize internal banners over "icons as banners"
        BannerContentElement.getInternalFallbackImageData(this.getModId()).ifPresent(triple -> {
            if (this.banners.isEmpty()) // Non-destructive design, if there is a user specified banner, we don't overwrite the blur
                this.blurBanner = Optional.of(triple.getThird());
            this.banners.add(Pair.of(triple.getFirst(), triple.getSecond()));
        });

        // [######| Forge |######]
        // Note Banners: Only checks for explicitly disabled blur, as default on forge is blurred.
        // If a banner doesn't have a specification or is set to true, we use the default
        // blur logic, which blurs it based on size.
        // Note Icons: Since the forge logo renderer is a lot bigger than the icons rendered PaCo uses, we ignore
        // forge's blur setting fully, and utilize the PaCo specification or size if the former is missing.
        if (this.banners.isEmpty() && !modInfo.getLogoBlur()) // Non-destructive design, if there is a user/internal specified banner, we don't overwrite the blur, also only checks for blur = false
            this.blurBanner = Optional.of(false); // Blur Banner
        modInfo.getLogoFile().ifPresent(logo -> {
            this.icons.add(Pair.of(this.getModId(), logo));   // Icon
            this.banners.add(Pair.of(this.getModId(), logo)); // Banner
        });

        // [######| Internal Fallback Textures |######]
        // TODO: probably need to move this to a different class to prevent class loader from screaming due to button being client only
        // Icons
        ModButton.getInternalFallbackResourceLocation(this.getModId()).ifPresent(this.icons::add);
        // Backgrounds
        BackgroundContentElement.getInternalFallbackImageData(this.getModId()).ifPresent(triple -> {
            if (this.backgrounds.isEmpty()) // Non-destructive design, if there is a user specified background, we don't overwrite the blur
                this.blurBackground = Optional.of(triple.getThird());
            this.backgrounds.add(Pair.of(triple.getFirst(), triple.getSecond()));
        });

        // [######| Missing Textures |######]
        // Backgrounds
        Triple<String, String, Boolean> triple = BackgroundContentElement.MOD_MISSING_BACKGROUNDS.get(Math.abs(this.getModId().hashCode()) % BackgroundContentElement.MOD_MISSING_BACKGROUNDS.size());
        if (this.backgrounds.isEmpty()) // Non-destructive design, if there is a user/internal specified background, we don't overwrite the blur
            this.blurBackground = Optional.of(triple.getThird());
        this.backgrounds.add(Pair.of(triple.getFirst(), triple.getSecond()));

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
    public List<Pair<String, String>> getModIconFiles() {
        return this.icons;
    }

    @Override
    public List<Pair<String, String>> getModBackgroundFiles() {
        return this.backgrounds;
    }

    @Override
    public List<Pair<String, String>> getModBannerFiles() {
        return this.banners;
    }

    @Override
    public Optional<Boolean> getBlurModIcon() {
        return this.blurIcon;
    }

    @Override
    public Optional<Boolean> getBlurModBackground() {
        return this.blurBackground;
    }

    @Override
    public Optional<Boolean> getBlurModBanner() {
        return this.blurBanner;
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

        // TODO: Maybe remove this ?
        // Since Mods arent inside the "mods" folder in IDEs this is used to manually check for files
        // inside a "libs" folder, which are then compared to the id of the current data holder.
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