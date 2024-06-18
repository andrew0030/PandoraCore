package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.platform.services.IPlatformHelper;
import com.github.andrew0030.pandora_core.utils.ModDataHolder;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.function.Consumer;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void loadNativeImage(String modId, String resource, Consumer<NativeImage> consumer) {
        FabricLoader.getInstance().getModContainer(modId).flatMap(container -> container.findPath(resource)).ifPresent(path -> {
            try(InputStream is = Files.newInputStream(path); NativeImage icon = NativeImage.read(is)) {
                consumer.accept(icon);
            } catch(IOException ignored) {}
        });
    }

    @Override
    public ModDataHolder getModDataHolder(String modId) {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);
        if (modContainer.isPresent()) {
            ModMetadata metadata = modContainer.get().getMetadata();
            ModDataHolder holder = ModDataHolder.forMod(modId);
            holder.setModName(metadata.getName());
            holder.setModVersion(metadata.getVersion().getFriendlyString());
            holder.setModIconFile(metadata.getIconPath(0).orElse(null));

            return holder;
        }
        PandoraCore.LOGGER.warn("Couldn't get ModContainer for: '{}', returning empty ModDataHolder!", modId);
        return ModDataHolder.forMod(modId);
    }
}