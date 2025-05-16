package com.github.andrew0030.pandora_core.client.ctm;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            if (!(json.data() instanceof JsonObject obj)) continue;
            if (!obj.has("pandora_core:overwrite")) continue;
            JsonObject overwrite = GsonHelper.getAsJsonObject(obj, "pandora_core:overwrite");

            for (Map.Entry<String, JsonElement> entry : overwrite.entrySet()) {
                String from = entry.getKey();
                String to = entry.getValue().getAsString();
                try {
                    map.put(new ResourceLocation(from), new ResourceLocation(to));
                } catch (Exception ignored) {}
            }
        }

        return map;
    }

    public static @Nullable Set<Property<?>> getPropertiesToCheck(ResourceLocation modelId) {
        if (CTMJsonHelper.blockStateJsonSupplier == null) return null;
        List<ModelBakery.LoadedJson> jsons = CTMJsonHelper.blockStateJsonSupplier.apply(CTMJsonHelper.convertModelIdToBlockStatePath(modelId));
        if (jsons == null) return null;

        for (ModelBakery.LoadedJson json : jsons) {
            if (!(json.data() instanceof JsonObject obj)) continue;
            if (!obj.has("pandora_core:properties")) continue;
            JsonElement properties = obj.get("pandora_core:properties");
            Optional<Block> optionalBlock = BuiltInRegistries.BLOCK.getOptional(new ResourceLocation(modelId.getNamespace(), modelId.getPath()));
            if (optionalBlock.isEmpty()) continue;

            Collection<Property<?>> allProps = optionalBlock.get().defaultBlockState().getProperties();
            Set<String> names = new HashSet<>();

            if (properties.isJsonPrimitive()) {
                names.add(properties.getAsString());
            } else if (properties.isJsonArray()) {
                for (JsonElement el : properties.getAsJsonArray())
                    if (el.isJsonPrimitive())
                        names.add(el.getAsString());
            }

            if (!names.isEmpty())
                return allProps.stream().filter(prop -> names.contains(prop.getName())).collect(Collectors.toSet());
        }

        return null;
    }

    public static @Nullable HolderSet<Block> getConnectsWith(ResourceLocation modelId) {
        if (CTMJsonHelper.blockStateJsonSupplier == null) return null;
        List<ModelBakery.LoadedJson> jsons = CTMJsonHelper.blockStateJsonSupplier.apply(CTMJsonHelper.convertModelIdToBlockStatePath(modelId));
        if (jsons == null) return null;

        for (ModelBakery.LoadedJson json : jsons) {
            if (!(json.data() instanceof JsonObject obj)) continue;
            if (!obj.has("pandora_core:connect")) continue;
            JsonElement connect = obj.get("pandora_core:connect");

            // Top-level tag
            if (connect.isJsonPrimitive()) {
                String value = connect.getAsString();
                if (value.startsWith("#")) {
                    try {
                        ResourceLocation tagName = new ResourceLocation(value.substring(1));
                        TagKey<Block> tag = TagKey.create(Registries.BLOCK, tagName);
                        return HolderSet.emptyNamed(BuiltInRegistries.BLOCK.asLookup(), tag);
                    } catch (Exception ignored) {}
                }
            }

            // Array or plain block
            Set<Block> uniqueBlocks = new HashSet<>();
            if (connect.isJsonPrimitive()) {
                CTMJsonHelper.addBlockIfValid(connect.getAsString(), uniqueBlocks);
            } else if (connect.isJsonArray()) {
                for (JsonElement el : connect.getAsJsonArray())
                    if (el.isJsonPrimitive())
                        CTMJsonHelper.addBlockIfValid(el.getAsString(), uniqueBlocks);
            }
            if (!uniqueBlocks.isEmpty()) {
                return HolderSet.direct(uniqueBlocks.stream()
                        .map(Block::builtInRegistryHolder)
                        .toList());
            }
        }

        return null;
    }

    private static void addBlockIfValid(String name, Set<Block> uniqueBlocks) {
        try {
            ResourceLocation id = new ResourceLocation(name);
            BuiltInRegistries.BLOCK.getOptional(id).ifPresent(block -> {
                if (block != Blocks.AIR)
                    uniqueBlocks.add(block);
            });
        } catch (Exception ignored) {}
    }

    private static ResourceLocation convertModelIdToBlockStatePath(ResourceLocation modelId) {
        return new ResourceLocation(modelId.getNamespace(), "blockstates/" + modelId.getPath() + ".json");
    }
}