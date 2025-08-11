package com.github.andrew0030.pandora_core.test.entity;

import com.github.andrew0030.pandora_core.PandoraCore;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import org.jetbrains.annotations.NotNull;

public class TestEntityRenderer<E extends Chicken> extends MobRenderer<E, ChickenModel<E>> {
    private final ResourceLocation TEXTURE = new ResourceLocation(PandoraCore.MOD_ID, "textures/entity/test_entity.png");

    public TestEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull E entity) {
        return TEXTURE;
    }
}