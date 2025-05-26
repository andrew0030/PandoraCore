package com.github.andrew0030.pandora_core.mixin.ctm;

import com.github.andrew0030.pandora_core.client.ctm.CTMJsonHelper;
import com.github.andrew0030.pandora_core.client.ctm.CTMUnbakedModel;
import com.github.andrew0030.pandora_core.utils.mod_warnings.ModWarningFactory;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {
    @Shadow @Final private Map<ResourceLocation, List<ModelBakery.LoadedJson>> blockStateResources;

    @Inject(method = "loadTopLevel", at = @At("HEAD"))
    public void injectLoadTopLevel(ModelResourceLocation id, CallbackInfo ci) {
        if (id.equals(ModelBakery.MISSING_MODEL_LOCATION))
            CTMJsonHelper.setBlockStateJsonSupplier(blockStateResources::get);
    }

    @ModifyVariable(method = "cacheAndQueueDependencies", at = @At("HEAD"), argsOnly = true)
    public UnbakedModel modifyGetModel(UnbakedModel model, ResourceLocation id) {

        // TODO: add config option to disable CTM

        // If Sodium (pre 0.6.0) is installed and Indium isn't we skip CTM rendering as it wouldn't work
        // "isCTMDisabled" is initialized during setup so by the time it gets called here it's ready to go
        if (ModWarningFactory.isCTMDisabled)
            return model;
        // If the model has a CTM type and isn't rendered in the inventory we wrap it and use it for CTM rendering
        if (CTMJsonHelper.hasCTM(id) && !id.toString().endsWith("#inventory"))
            return new CTMUnbakedModel(model);
        return model;
    }
}