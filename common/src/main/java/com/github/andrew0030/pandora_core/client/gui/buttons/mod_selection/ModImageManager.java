package com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.function.TriConsumer;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ModImageManager implements Closeable {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "ModImageManager");
    private final Map<String, Pair<ResourceLocation, DynamicTexture>> iconCache = new HashMap<>();
    private Pair<String, Pair<ResourceLocation, DynamicTexture>> backgroundCache = Pair.of(null, Pair.of(null, null));
    private Pair<String, Pair<ResourceLocation, DynamicTexture>> bannerCache = Pair.of(null, Pair.of(null, null));
    // Just a boolean that can be toggled to get some debug info about icon caching.
    public static final boolean SHOW_DEBUG_MESSAGES = false;

    @Override
    public void close() {
        PaCoLogger.conditionalInfo(LOGGER, SHOW_DEBUG_MESSAGES, "Unloading all cached images...");
        // Mod Icons
        for (Map.Entry<String, Pair<ResourceLocation, DynamicTexture>> entry : this.iconCache.entrySet()) {
            // If a mod failed loading, or its logo isn't 1:1 there is no DynamicTexture that needs closing.
            if (entry.getValue().getSecond() != null)
                entry.getValue().getSecond().close();
            PaCoLogger.conditionalInfo(LOGGER, SHOW_DEBUG_MESSAGES, "[{}] Unloading cached icon image", entry.getKey());
        }
        // Mod Background
        if (this.backgroundCache.getSecond() != null) {
            if (this.backgroundCache.getSecond().getSecond() != null)
                this.backgroundCache.getSecond().getSecond().close();
            PaCoLogger.conditionalInfo(LOGGER, SHOW_DEBUG_MESSAGES, "[{}] Unloading cached background image", this.backgroundCache.getFirst());
        }
        // Mod Banner
        if (this.bannerCache.getSecond() != null) {
            if (this.bannerCache.getSecond().getSecond() != null)
                this.bannerCache.getSecond().getSecond().close();
            PaCoLogger.conditionalInfo(LOGGER, SHOW_DEBUG_MESSAGES, "[{}] Unloading cached banner image", this.bannerCache.getFirst());
        }
    }

    /** @return Whether there is an icon cache entry, for the given mod id. */
    public boolean isIconPresent(String modId) {
        return this.iconCache.containsKey(modId);
    }

    /** @return The cached icon of the given mod id. If there is none null is returned. */
    @Nullable
    public Pair<ResourceLocation, DynamicTexture> getCachedIcon(String modId) {
        return this.iconCache.get(modId);
    }

    /** @return The cached background of the given mod id. If there is none null is returned. */
    @Nullable
    public Pair<ResourceLocation, DynamicTexture> getCachedBackground(String modId) {
        if (this.backgroundCache.getFirst() == null) return null;
        return this.backgroundCache.getFirst().equals(modId) ? this.backgroundCache.getSecond() : null;
    }

    /** @return The cached banner of the given mod id. If there is none null is returned. */
    @Nullable
    public Pair<ResourceLocation, DynamicTexture> getCachedBanner(String modId) {
        if (this.bannerCache.getFirst() == null) return null;
        return this.bannerCache.getFirst().equals(modId) ? this.bannerCache.getSecond() : null;
    }

    /** Caches the given icon image data for the given mod id. */
    public void cacheIcon(String modId, ResourceLocation resourceLocation, DynamicTexture dynamicTexture) {
        this.iconCache.put(modId, Pair.of(resourceLocation, dynamicTexture));
        PaCoLogger.conditionalInfo(LOGGER, SHOW_DEBUG_MESSAGES, "[{}] Loading icon image into cache", modId);
    }

    /**
     * Caches the given background image data for the given mod id.<br/>
     * Note: Since by design there can only be one background loaded at a time,
     * this also unloads the previous background if there was one.
     */
    public void cacheBackground(String modId, ResourceLocation resourceLocation, DynamicTexture dynamicTexture) {
        if (this.backgroundCache.getSecond() != null) {
            if (this.backgroundCache.getSecond().getSecond() != null)
                this.backgroundCache.getSecond().getSecond().close();
            PaCoLogger.conditionalInfo(LOGGER, SHOW_DEBUG_MESSAGES, "[{}] Unloading cached background image", this.backgroundCache.getFirst());
        }
        this.backgroundCache = Pair.of(modId, Pair.of(resourceLocation, dynamicTexture));
        PaCoLogger.conditionalInfo(LOGGER, SHOW_DEBUG_MESSAGES, "[{}] Loading background image into cache", modId);
    }

    /**
     * Caches the given banner image data for the given mod id.<br/>
     * Note: Since by design there can only be one banner loaded at a time,
     * this also unloads the previous banner if there was one.
     */
    public void cacheBanner(String modId, ResourceLocation resourceLocation, DynamicTexture dynamicTexture) {
        if (this.bannerCache.getSecond() != null) {
            if (this.bannerCache.getSecond().getSecond() != null)
                this.bannerCache.getSecond().getSecond().close();
            PaCoLogger.conditionalInfo(LOGGER, SHOW_DEBUG_MESSAGES, "[{}] Unloading cached banner image", this.bannerCache.getFirst());
        }
        this.bannerCache = Pair.of(modId, Pair.of(resourceLocation, dynamicTexture));
        PaCoLogger.conditionalInfo(LOGGER, SHOW_DEBUG_MESSAGES, "[{}] Loading banner image into cache", modId);
    }

    /**
     * Used to load and cache an image out of a given list for a given mod.<br/>
     * Note: this method stops once a valid image has been loaded, so the list order matters.
     * @param modId       The mod id of the mod containing the images we are trying to load
     * @param getCached   A {@link Function} used to load a cached image
     * @param cache       A {@link TriConsumer} used to cache an image
     * @param imageFiles  A {@link List} containing strings pointing to images that should be loaded
     * @param aspectRatio The aspect, a valid image needs to have
     * @param blur        A {@link BiFunction} that takes the width and height, and returns whether this image should be blurred when scaled
     * @param type        A {@link String} describing what the purpose of this image is. This is combined with "mod" to create a key.
     *                    For example "icon" will become "modicon", or "background" will become "modbackground".
     * @return A {@link Pair} containing the {@link ResourceLocation} of the image, and additionally a second {@link Pair}
     * containing the <strong>width</strong> and <strong>height</strong> of the image.<br/>
     * If no image successfully loaded, <strong>null</strong> is returned and cached.
     */
    @Nullable
    public Pair<ResourceLocation, Pair<Integer, Integer>> getImageData(
            String modId,
            Function<String, Pair<ResourceLocation, DynamicTexture>> getCached,
            TriConsumer<String, ResourceLocation, DynamicTexture> cache,
            List<String> imageFiles,
            float aspectRatio,
            BiFunction<Integer, Integer, Boolean> blur,
            String type
    ) {
        return this.getImageData(modId, getCached, cache, imageFiles, aspectRatio, aspectRatio, blur, type);
    }

    /**
     * Used to load and cache an image out of a given list for a given mod.<br/>
     * Note: this method stops once a valid image has been loaded, so the list order matters.
     * @param modId          The mod id of the mod containing the images we are trying to load
     * @param getCached      A {@link Function} used to load a cached image
     * @param cache          A {@link TriConsumer} used to cache an image
     * @param imageFiles     A {@link List} containing strings pointing to images that should be loaded
     * @param minAspectRatio The minimum aspect ratio (inclusive), a valid image can be
     * @param maxAspectRatio The maximum aspect ratio (inclusive), a valid image can be
     * @param blur           A {@link BiFunction} that takes the width and height, and returns whether this image should be blurred when scaled
     * @param type           A {@link String} describing what the purpose of this image is. This is combined with "mod" to create a key.
     *                       For example "icon" will become "modicon", or "background" will become "modbackground".
     * @return A {@link Pair} containing the {@link ResourceLocation} of the image, and additionally a second {@link Pair}
     * containing the <strong>width</strong> and <strong>height</strong> of the image.<br/>
     * If no image successfully loaded, <strong>null</strong> is returned and cached.
     */
    @Nullable
    public Pair<ResourceLocation, Pair<Integer, Integer>> getImageData(
            String modId,
            Function<String, Pair<ResourceLocation, DynamicTexture>> getCached,
            TriConsumer<String, ResourceLocation, DynamicTexture> cache,
            List<String> imageFiles,
            float minAspectRatio,
            float maxAspectRatio,
            BiFunction<Integer, Integer, Boolean> blur,
            String type
    ) {
        // Checks if the modId is already present in the cache, and grabs the values if so.
        Pair<ResourceLocation, DynamicTexture> cachedEntry = getCached.apply(modId);
        if (cachedEntry != null) {
            // If the cached entry's resource location is null, then null should be returned for consistency reasons
            // The first entry being null indicates that the mod has an image, but said image failed to load
            if (cachedEntry.getFirst() == null)
                return null;

            // return the entry
            return Pair.of(
                    cachedEntry.getFirst(),
                    Pair.of(cachedEntry.getSecond().getPixels().getWidth(), cachedEntry.getSecond().getPixels().getHeight())
            );
        }

        // If no image is already cached, we attempt to load one, and stop if a valid one was found
        // Alternatively if there is no images (the list is empty) the loop will not get called and null is cached/returned
        for (String imageFile : imageFiles) {
            Pair<ResourceLocation, Pair<Integer, Integer>> result = Services.PLATFORM.loadNativeImage(modId, imageFile, nativeImage -> {
                // If the backgroundFile fails to load, null is provided
                if (nativeImage == null)
                    return null;
                // If the image aspect ratio is out of bounds, null is provided
                float aspectRatio = (float) nativeImage.getWidth() / nativeImage.getHeight();
                if (aspectRatio < minAspectRatio || aspectRatio > maxAspectRatio)
                    return null;

                // Determines whether to blur the image using the provided BiFunction
                boolean shouldBlur = blur.apply(nativeImage.getWidth(), nativeImage.getHeight());
                DynamicTexture dynamicTexture = null;
                try {
                    dynamicTexture = new DynamicTexture(nativeImage) {
                        @Override
                        public void upload() {
                            this.bind();
                            NativeImage image = this.getPixels();
                            this.getPixels().upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), shouldBlur, false, false, false);
                        }
                    };
                    // Register and cache the texture, and return it
                    ResourceLocation resourceLocation = Minecraft.getInstance().getTextureManager().register("mod" + type, dynamicTexture);
                    cache.accept(modId, resourceLocation, dynamicTexture);
                    return Pair.of(resourceLocation, Pair.of(nativeImage.getWidth(), nativeImage.getHeight()));
                } catch (Exception ignored) {
                    // Safety reasons
                    // To my knowledge, the try here should never fail
                    // However, VRAM leaks are particularly annoying in that they're unnoticeable until the device crashes, at which point the screen blacks out until drivers reboot
                    if (dynamicTexture != null)
                        dynamicTexture.close();
                    return null; // Return null to indicate failure for this image
                }
            });
            // If we found a valid image, we return it
            // This will stop the loop as we don't need to look at other entries
            if (result != null)
                return result;
        }
        // If we went through all the images and none succeeded or were valid,
        // we cache null,null to indicate that and return null
        cache.accept(modId, null, null);
        return null;
    }
}