package com.github.andrew0030.pandora_core.client.ctm;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.List;
import java.util.function.Function;

public class PaCoModelData {
    private static Function<ResourceLocation, List<ModelBakery.LoadedJson>> getter;

    /**
     * Sets the internal function used to retrieve block state JSON data by ResourceLocation.
     *
     * @param getter The getter function that maps model IDs to their LoadedJson representations.
     */
    public static void setGetter(Function<ResourceLocation, List<ModelBakery.LoadedJson>> getter) {
        PaCoModelData.getter = getter;
    }

    //TODO expand to handle sprites (maybe cache them?)
    public static boolean hasCTM(ResourceLocation modelId) {
        if (getter == null) return false;
        List<ModelBakery.LoadedJson> jsons = PaCoModelData.getter.apply(PaCoModelData.convertModelIdToBlockStatePath(modelId));
        if (jsons == null) return false;

        for (ModelBakery.LoadedJson json : jsons) {
            if (json.data() instanceof JsonObject object) {
                if (GsonHelper.getAsBoolean(object, "pandora_core:ctm", false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ResourceLocation convertModelIdToBlockStatePath(ResourceLocation modelId) {
        return new ResourceLocation(modelId.getNamespace(), "blockstates/" + modelId.getPath() + ".json");
    }
}