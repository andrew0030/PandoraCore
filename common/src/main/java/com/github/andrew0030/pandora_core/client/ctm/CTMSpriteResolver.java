package com.github.andrew0030.pandora_core.client.ctm;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CTMSpriteResolver {
    private final Map<TextureAtlasSprite, Int2ObjectMap<SpriteResultHolder>> sprites = new HashMap<>();
    private SpriteResultHolder missingResult;

    private CTMSpriteResolver() {}

    public static CTMSpriteResolver from(Function<Material, TextureAtlasSprite> spriteGetter, ResourceLocation modelId) {
        CTMSpriteResolver spriteResolver = new CTMSpriteResolver();
        spriteResolver.missingResult = new SpriteResultHolder(spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation())), true);

        CTMJsonHelper.getTextureOverrides(modelId).forEach((key, value) -> {
            Material matFrom = new Material(InventoryMenu.BLOCK_ATLAS, key);
            TextureAtlasSprite from = spriteGetter.apply(matFrom);

            // If the key is missing we don't add the entry
            // As we don't want to have "null -> sprite"
            if (from == spriteResolver.missingResult.get())
                return;

            Int2ObjectMap<SpriteResultHolder> results = new Int2ObjectOpenHashMap<>();
            if (value.toString().endsWith("/")) {
                // TODO probably modify this to take into account CTM type
                for (int i = 0; i < 47; i++) {
                    Material matTo = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(value.toString() + i));
                    TextureAtlasSprite to = spriteGetter.apply(matTo);
                    boolean missing = to == spriteResolver.missingResult.get();
                    results.put(i, new SpriteResultHolder(to, missing));
                }
            } else {
                Material matTo = new Material(InventoryMenu.BLOCK_ATLAS, value);
                TextureAtlasSprite to = spriteGetter.apply(matTo);
                boolean missing = to == spriteResolver.missingResult.get();
                results.put(0, new SpriteResultHolder(to, missing));
            }

            spriteResolver.sprites.put(from, results);
        });

        return spriteResolver;
    }

    public @Nullable SpriteResultHolder get(TextureAtlasSprite sprite, int index) {
        Int2ObjectMap<SpriteResultHolder> holders = this.sprites.get(sprite);
        if (holders == null) return null;
        // If only one texture was used (a sheet) we return it as is
        if (holders.size() == 1)
            return holders.get(0);
        // If multiple textures were used we grab the specified index
        return holders.get(index);
    }

    /** @return Whether there is multiple {@link SpriteResultHolder} instances. */
    public boolean usesMultipleSprites(TextureAtlasSprite sprite) {
        Int2ObjectMap<SpriteResultHolder> holders = this.sprites.get(sprite);
        return holders != null && holders.size() > 1;
    }

    public static class SpriteResultHolder {
        private final TextureAtlasSprite sprite;
        private final boolean missing;

        public SpriteResultHolder(TextureAtlasSprite sprite, boolean missing) {
            this.sprite = sprite;
            this.missing = missing;
        }

        public boolean isMissing() {
            return this.missing;
        }

        public TextureAtlasSprite get() {
            return this.sprite;
        }
    }
}