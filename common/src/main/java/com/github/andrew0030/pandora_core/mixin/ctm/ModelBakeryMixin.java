package com.github.andrew0030.pandora_core.mixin.ctm;

import com.github.andrew0030.pandora_core.client.ctm.PaCoModelData;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {
    @Shadow @Final private Map<ResourceLocation, List<ModelBakery.LoadedJson>> blockStateResources;

    @Inject(method = "loadTopLevel", at = @At("HEAD"))
    public void injectLoadTopLevel(ModelResourceLocation id, CallbackInfo ci) {
        if (id.equals(ModelBakery.MISSING_MODEL_LOCATION)) {
            PaCoModelData.setGetter(blockStateResources::get);
        }
    }
}