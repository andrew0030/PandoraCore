package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.platform.services.IPlatformHelper;
import com.github.andrew0030.pandora_core.utils.ModDataHolder;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IModInfo;
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

    @Override
    public ModDataHolder getModDataHolder(String modId) {
        Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(modId);
        if (modContainer.isPresent()) {
            IModInfo modInfo = modContainer.get().getModInfo();
            ModDataHolder holder = ModDataHolder.forMod(modId);
            holder.setModName(modInfo.getDisplayName());
            holder.setModVersion(modInfo.getVersion().toString());
            holder.setModIconFile(modInfo.getLogoFile().orElse(null));

            return holder;
        }
        PandoraCore.LOGGER.warn("Couldn't get ModContainer for: '{}', returning empty ModDataHolder!", modId);
        return ModDataHolder.forMod(modId);
    }
}