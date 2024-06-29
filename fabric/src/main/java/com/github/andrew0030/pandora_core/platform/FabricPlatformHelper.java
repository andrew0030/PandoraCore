package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.platform.services.IPlatformHelper;
import com.github.andrew0030.pandora_core.utils.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.function.Function;

public class FabricPlatformHelper implements IPlatformHelper {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "FabricPlatformHelper");

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
    public <T> T loadNativeImage(String modId, String resource, Function<NativeImage, T> consumer) {
        Object[] o = new Object[1];
        FabricLoader.getInstance().getModContainer(modId).flatMap(container -> container.findPath(resource)).ifPresent(path -> {
            try(InputStream is = Files.newInputStream(path); NativeImage icon = NativeImage.read(is)) {
                o[0] = (T) consumer.apply(icon);
            } catch(IOException ignored) {
                // If the icon fails to load, provide a null texture so that it can be cached as attempted
                o[0] = consumer.apply(null);
            }
        });
        return (T) o[0];
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
        LOGGER.warn("Couldn't get ModContainer for: '{}', returning empty ModDataHolder!", modId);
        return ModDataHolder.forMod(modId);
    }
}