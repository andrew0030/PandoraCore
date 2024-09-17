package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.platform.services.IPlatformHelper;
import com.github.andrew0030.pandora_core.utils.data_holders.FabricModDataHolder;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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
    public List<ModDataHolder> getModDataHolders() {
        List<ModDataHolder> holders = new ArrayList<>();
        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            ModMetadata metadata = modContainer.getMetadata();
            // Prevents libraries from being added to the list.
            if(metadata.getCustomValue("fabric-api:module-lifecycle") != null)
                return;
            if(metadata.getId().equals("java") || metadata.getId().equals("mixinextras"))
                return;
            CustomValue generated = metadata.getCustomValue("fabric-loom:generated");
            if(generated != null && generated.getType() == CustomValue.CvType.BOOLEAN && generated.getAsBoolean())
                return;
            holders.add(new FabricModDataHolder(metadata));
        });
        return holders;
    }
}