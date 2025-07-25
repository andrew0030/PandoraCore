package com.github.andrew0030.pandora_core.utils.data_holders;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

public class FabricModDataHolder extends ModDataHolder {
    private static final List<String> MOJANG_STUDIOS = List.of("Mojang Studios");
    private static final String MINECRAFT_EULA = "Minecraft EULA";
    private static final String MINECRAFT_DESCRIPTION = "The base game.";
    private final ModContainer container;
    private final ModMetadata metadata;
    private final List<String> icons = new ArrayList<>();
    private Optional<Boolean> blurIcon = Optional.empty();
    private final List<String> backgrounds = new ArrayList<>();
    private Optional<URL> updateURL = Optional.empty();
    private Supplier<List<Component>> modWarnings;
    private final List<String> authors = new ArrayList<>();
    private final List<String> credits = new ArrayList<>();

    public FabricModDataHolder(ModContainer modContainer) {
        this.container = modContainer;
        this.metadata = modContainer.getMetadata();
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
                                this.modWarnings = this.loadWarningsFromFactory(factoryClass);
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

        this.authors.addAll(this.metadata.getAuthors().stream().map(Person::getName).toList());
        this.credits.addAll(this.metadata.getContributors().stream().map(Person::getName).toList());
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
        if (this.getModId().equals("minecraft")) return MINECRAFT_DESCRIPTION;
        return this.metadata.getDescription();
    }

    @Override
    public List<String> getModAuthors() {
        if (this.getModId().equals("minecraft")) return MOJANG_STUDIOS;
        return this.authors;
    }

    @Override
    public List<String> getModCredits() {
        return this.credits;
    }

    @Override
    public String getModLicense() {
        if (this.getModId().equals("minecraft")) return MINECRAFT_EULA;
        return StringUtils.join(this.metadata.getLicense(), ", ");
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
        if (this.modWarnings == null) return NO_WARNINGS;
        return this.modWarnings.get();
    }

    @Override
    public Optional<String> getSha512Hash() {
        // If the jar is part of a fatjar the user cant update it so there is no point in checking it
        if (this.container.getContainingMod().isPresent()) return Optional.empty();;
        // If the origin isn't a Path we can't check and return early
        if (this.container.getOrigin().getKind() != ModOrigin.Kind.PATH) return Optional.empty();;
        // After ensuring the origin is a Path we make sure it's a .jar and not a folder
        List<Path> paths = this.container.getOrigin().getPaths();
        Optional<Path> optionalPath = paths.stream()
                .filter(path -> path.toString().toLowerCase(Locale.ROOT).endsWith(".jar"))
                .findFirst();
        // If the path wasn't a jar or was invalid we return early
        if (optionalPath.isEmpty()) return Optional.empty();;
        // Lastly if the file is a valid file, we read it and hash it using SHA512
        File file = optionalPath.get().toFile();
        if (file.isFile()) {
            try {
                return Optional.of(Files.asByteSource(file).hash(Hashing.sha512()).toString());
            } catch (IOException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}