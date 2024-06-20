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
import java.util.function.Function;

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
    public <T> T loadNativeImage(String modId, String resource, Function<NativeImage, T> consumer) {
        Object[] o = new Object[1];
        ResourcePackLoader.getPackFor(modId).ifPresent(resources -> {
            IoSupplier<InputStream> supplier = resources.getRootResource(resource);
            if(supplier != null) {
                try(InputStream is = supplier.get(); NativeImage image = NativeImage.read(is)) {
                    o[0] = consumer.apply(image);
                } catch(IOException ignored) {
                    // If the icon fails to load, provide a null texture so that it can be cached as attempted
                    o[0] = consumer.apply(null);
                }
            }
        });
        return (T) o[0];
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