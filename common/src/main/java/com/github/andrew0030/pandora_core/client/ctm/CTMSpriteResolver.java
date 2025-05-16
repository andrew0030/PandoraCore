package com.github.andrew0030.pandora_core.client.ctm;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CTMSpriteResolver {
    private final Map<TextureAtlasSprite, SpriteResultHolder> sprites = new HashMap<>();
    private SpriteResultHolder missingResult;

    private CTMSpriteResolver() {}

    public static CTMSpriteResolver from(Function<Material, TextureAtlasSprite> spriteGetter, ResourceLocation modelId) {
        CTMSpriteResolver spriteResolver = new CTMSpriteResolver();
        spriteResolver.missingResult = new SpriteResultHolder(spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation())), true);

        CTMJsonHelper.getCTMTextureOverrides(modelId).forEach((key, value) -> {
            Material matFrom = new Material(InventoryMenu.BLOCK_ATLAS, key);
            Material matTo = new Material(InventoryMenu.BLOCK_ATLAS, value);

            TextureAtlasSprite from = spriteGetter.apply(matFrom);
            TextureAtlasSprite to = spriteGetter.apply(matTo);
            boolean missing = to == spriteResolver.missingResult.get();

            // If the key is missing we don't add the entry
            // As we don't want to have "null -> sprite"
            if (from != spriteResolver.missingResult.get())
                spriteResolver.sprites.put(from, new SpriteResultHolder(to, missing));
        });

        return spriteResolver;
    }

    public SpriteResultHolder get(TextureAtlasSprite sprite) {
        return this.sprites.get(sprite);
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