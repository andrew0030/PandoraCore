package com.github.andrew0030.pandora_core.client.ctm;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class CTMUnbakedModel implements UnbakedModel {
    protected final UnbakedModel unbakedModel;

    public CTMUnbakedModel(UnbakedModel unbakedModel) {
        this.unbakedModel = unbakedModel;
    }

    @Override
    public @NotNull Collection<ResourceLocation> getDependencies() {
        return List.of();
    }

    @Override
    public void resolveParents(@NotNull Function<ResourceLocation, UnbakedModel> function) {}

    @Nullable
    @Override
    public BakedModel bake(@NotNull ModelBaker baker, @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState state, @NotNull ResourceLocation location) {
        BakedModel model = this.unbakedModel.bake(baker, spriteGetter, state, location);
        CTMSpriteResolver spriteResolver = CTMSpriteResolver.from(spriteGetter, location);
        return Services.PLATFORM.getCTMModel(model, spriteResolver);
    }
}