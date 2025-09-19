package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.ctm.BaseCTMModel;
import com.github.andrew0030.pandora_core.client.ctm.CTMDataResolver;
import com.github.andrew0030.pandora_core.client.ctm.CTMSpriteResolver;
import com.github.andrew0030.pandora_core.client.ctm.FabricCTMModel;
import com.github.andrew0030.pandora_core.platform.services.IPlatformHelper;
import com.github.andrew0030.pandora_core.utils.data_holders.FabricModDataHolder;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.resources.model.BakedModel;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
    public String getMinecraftVersion() {
        return FabricLoader.getInstance().getModContainer("minecraft")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("Unknown Version");
    }

    @Override
    public Path getGameDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public <T> T loadNativeImage(String modId, String resource, Function<NativeImage, T> consumer) {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);
        if (modContainer.isEmpty()) return null; // If no mod container with the given id exists we return early
        Optional<Path> path = modContainer.get().findPath(resource);
        T result = null;
        if (path.isPresent()) {
            try(InputStream is = Files.newInputStream(path.get()); NativeImage image = NativeImage.read(is)) {
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
            holders.add(new FabricModDataHolder(modContainer));
        });
        return holders;
    }

    @Override
    public BaseCTMModel getCTMModel(BakedModel model, CTMSpriteResolver spriteResolver, CTMDataResolver dataResolver) {
        return new FabricCTMModel(model, spriteResolver, dataResolver);
    }
}