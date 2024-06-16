package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.platform.services.IPlatformHelper;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.resource.ResourcePackLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Optional<String> getModLogoFile(String modId) {
        return ModList.get().getModContainerById(modId).flatMap(modContainer -> modContainer.getModInfo().getLogoFile());
    }

    @Override
    public void loadNativeImage(String modId, String resource, Consumer<NativeImage> consumer) {
        ResourcePackLoader.getPackFor(modId).ifPresent(resources -> {
            IoSupplier<InputStream> supplier = resources.getRootResource(resource);
            if(supplier != null) {
                try(InputStream is = supplier.get(); NativeImage image = NativeImage.read(is)) {
                    consumer.accept(image);
                } catch(IOException ignored) {}
            }
        });
    }
}