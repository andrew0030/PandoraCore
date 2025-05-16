package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.ctm.BaseCTMModel;
import com.github.andrew0030.pandora_core.client.ctm.CTMDataResolver;
import com.github.andrew0030.pandora_core.client.ctm.CTMSpriteResolver;
import com.github.andrew0030.pandora_core.client.ctm.ForgeCTMModel;
import com.github.andrew0030.pandora_core.platform.services.IPlatformHelper;
import com.github.andrew0030.pandora_core.utils.data_holders.ForgeModDataHolder;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.ResourcePackLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ForgePlatformHelper implements IPlatformHelper {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "ForgePlatformHelper");

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
    public String getMinecraftVersion() {
        return FMLLoader.versionInfo().mcVersion();
    }

    @Override
    public Path getGameDirectory() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
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
    public List<ModDataHolder> getModDataHolders() {
        List<ModDataHolder> holders = new ArrayList<>();
        ModList.get().forEachModContainer((s, modContainer) -> {
            IModInfo modInfo = modContainer.getModInfo();
            // Prevents libraries from being added to the list.
            if(modInfo.getOwningFile().getFile().getType() != IModFile.Type.MOD)
                return;
            holders.add(new ForgeModDataHolder(modInfo));
        });
        return holders;
    }

    @Override
    public BaseCTMModel getCTMModel(BakedModel model, CTMSpriteResolver spriteResolver, CTMDataResolver dataResolver) {
        return new ForgeCTMModel(model, spriteResolver, dataResolver);
    }
}