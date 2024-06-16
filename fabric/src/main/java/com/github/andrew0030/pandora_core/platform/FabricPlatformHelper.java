package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.platform.services.IPlatformHelper;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.api.FabricLoader;

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
    public Optional<String> getModLogoFile(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).flatMap(container -> container.getMetadata().getIconPath(0));
    }

    @Override
    public void loadNativeImage(String modId, String resource, Consumer<NativeImage> consumer) {
        FabricLoader.getInstance().getModContainer(modId).flatMap(container -> container.findPath(resource)).ifPresent(path -> {
            try(InputStream is = Files.newInputStream(path); NativeImage icon = NativeImage.read(is)) {
                consumer.accept(icon);
            } catch(IOException ignored) {}
        });
    }
}