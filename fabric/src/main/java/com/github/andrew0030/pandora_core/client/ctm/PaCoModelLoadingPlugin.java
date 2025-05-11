package com.github.andrew0030.pandora_core.client.ctm;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;

public class PaCoModelLoadingPlugin implements ModelLoadingPlugin {

    @Override
    public void onInitializeModelLoader(Context context) {
        context.modifyModelAfterBake().register((original, contextData) -> {
            ResourceLocation id = contextData.id();
            if (PaCoModelData.hasCTM(id) && !id.toString().endsWith("#inventory")) {
                return new FabricCTModel(original);
            }
            return original;
        });
    }
}