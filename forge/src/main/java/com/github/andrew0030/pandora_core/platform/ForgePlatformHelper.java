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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
        IModFileInfo modFileInfo = ModList.get().getModFileById(modId);
        if (modFileInfo == null) return null; // If no mod file with the given id exists we return early
        Path path = modFileInfo.getFile().findResource(resource);
        T result = null;
        if(Files.exists(path)) {
            try(InputStream is = Files.newInputStream(path); NativeImage image = NativeImage.read(is)) {
                result = consumer.apply(image);
            } catch(IOException ignored) {
                // If the image fails to load, provide a null texture so that it can be cached as attempted
                result = consumer.apply(null);
            }
        }
        return result;
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