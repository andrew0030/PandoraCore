package com.github.andrew0030.pandora_core.client.ctm;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CTMJsonHelper {
    private static Function<ResourceLocation, List<ModelBakery.LoadedJson>> blockStateJsonSupplier;

    /**
     * Sets the internal function used to supply raw block state JSON data for a given {@link ResourceLocation}.
     *
     * @param blockStateJsonSupplier A function that takes a model {@link ResourceLocation} and returns
     *                               a list of {@link ModelBakery.LoadedJson} objects representing the parsed
     *                               block state JSON definitions.
     */
    public static void setBlockStateJsonSupplier(Function<ResourceLocation, List<ModelBakery.LoadedJson>> blockStateJsonSupplier ) {
        CTMJsonHelper.blockStateJsonSupplier = blockStateJsonSupplier;
    }

    public static boolean hasCTM(ResourceLocation modelId) {
        if (CTMJsonHelper.blockStateJsonSupplier == null) return false;
        List<ModelBakery.LoadedJson> jsons = CTMJsonHelper.blockStateJsonSupplier.apply(CTMJsonHelper.convertModelIdToBlockStatePath(modelId));
        if (jsons == null) return false;

        // TODO maybe expand this to handle some CTM types?
        for (ModelBakery.LoadedJson json : jsons) {
            if (json.data() instanceof JsonObject object) {
                if (GsonHelper.getAsBoolean(object, "pandora_core:ctm", false)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Map<ResourceLocation, ResourceLocation> getCTMTextureOverrides(ResourceLocation modelId) {
        Map<ResourceLocation, ResourceLocation> map = new HashMap<>();
        if (CTMJsonHelper.blockStateJsonSupplier == null) return map;
        List<ModelBakery.LoadedJson> jsons = CTMJsonHelper.blockStateJsonSupplier.apply(CTMJsonHelper.convertModelIdToBlockStatePath(modelId));
        if (jsons == null) return map;

        for (ModelBakery.LoadedJson json : jsons) {
            if (json.data() instanceof JsonObject object) {
                if (object.has("pandora_core:overwrite")) {
                    JsonObject overwrite = GsonHelper.getAsJsonObject(object, "pandora_core:overwrite");
                    for (Map.Entry<String, JsonElement> entry : overwrite.entrySet()) {
                        String from = entry.getKey();
                        String to = entry.getValue().getAsString();

//                        try {
                            map.put(new ResourceLocation(from), new ResourceLocation(to));
//                        } catch (Exception ignored) {
//                            // Optionally log a warning here
//                        }
                    }
                }
            }
        }

        return map;
    }

    private static ResourceLocation convertModelIdToBlockStatePath(ResourceLocation modelId) {
        return new ResourceLocation(modelId.getNamespace(), "blockstates/" + modelId.getPath() + ".json");
    }
}