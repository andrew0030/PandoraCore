package com.github.andrew0030.pandora_core.client.ctm;

import com.github.andrew0030.pandora_core.client.ctm.types.BaseCTMType;
import com.github.andrew0030.pandora_core.client.ctm.types.RandomCTMType;
import com.github.andrew0030.pandora_core.client.ctm.types.RepeatCTMType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
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

        for (ModelBakery.LoadedJson json : jsons) {
            if (!(json.data() instanceof JsonObject obj)) continue;
            return obj.has("pandora_core:ctm");
        }
        return false;
    }

    public static @Nullable BaseCTMType getCTMType(ResourceLocation modelId) {
        if (CTMJsonHelper.blockStateJsonSupplier == null) return null;
        List<ModelBakery.LoadedJson> jsons = CTMJsonHelper.blockStateJsonSupplier.apply(CTMJsonHelper.convertModelIdToBlockStatePath(modelId));
        if (jsons == null) return null;

        for (ModelBakery.LoadedJson json : jsons) {
            if (!(json.data() instanceof JsonObject obj)) continue;
            if (!obj.has("pandora_core:ctm")) continue;
            JsonElement type = obj.get("pandora_core:ctm");

            if (type.isJsonPrimitive()) {
                return CTMTypeManager.getCTMType(type.getAsString());
            } else if (type.isJsonObject()) {
                JsonObject typeObj = type.getAsJsonObject();
                JsonElement innerType = typeObj.get("type");

                int width = GsonHelper.getAsInt(typeObj, "width", 1);
                int height = GsonHelper.getAsInt(typeObj, "height", 1);

                if (innerType != null && innerType.isJsonPrimitive()) {
                    String typeString = innerType.getAsString();
                    // Random
                    if (typeString.equals("random")) {
                        RandomCTMType randomCTMType = new RandomCTMType();
                        randomCTMType.setDimensions(width, height);
                        return randomCTMType;
                    }
                    // Repeating
                    if (typeString.equals("repeat")) {
                        RepeatCTMType repeatCTMType = new RepeatCTMType();
                        repeatCTMType.setDimensions(width, height);
                        return repeatCTMType;
                    }
                }
            }
        }

        return null;
    }

    public static Map<ResourceLocation, ResourceLocation> getTextureOverrides(ResourceLocation modelId) {
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

            // Gets the block we are working with, this should technically not fail, but better safe than sorry
            Optional<Block> optionalBlock = BuiltInRegistries.BLOCK.getOptional(new ResourceLocation(modelId.getNamespace(), modelId.getPath()));
            if (optionalBlock.isEmpty()) continue;

            // Grabs all the properties the block has
            Collection<Property<?>> allProps = optionalBlock.get().defaultBlockState().getProperties();
            Set<String> names = new HashSet<>();

            // Grabs all the property strings specified to be checked
            if (properties.isJsonPrimitive()) {
                names.add(properties.getAsString());
            } else if (properties.isJsonArray()) {
                for (JsonElement el : properties.getAsJsonArray())
                    if (el.isJsonPrimitive())
                        names.add(el.getAsString());
            }

            // If any strings were found, we check if the block's properties contain them, by comparing them to the names
            if (!names.isEmpty())
                return allProps.stream().filter(prop -> names.contains(prop.getName())).collect(Collectors.toSet());
        }

        return null;
    }

    public static @Nullable EnumMap<Direction, FaceAdjacency.Mutation> getMutations(ResourceLocation modelId) {
        if (CTMJsonHelper.blockStateJsonSupplier == null) return null;
        List<ModelBakery.LoadedJson> jsons = CTMJsonHelper.blockStateJsonSupplier.apply(CTMJsonHelper.convertModelIdToBlockStatePath(modelId));
        if (jsons == null) return null;

        // Grabs the variant if the ResourceLocation is a ModelResourceLocation
        String variant = "";
        if (modelId instanceof ModelResourceLocation mrl)
            variant = mrl.getVariant();

        for (ModelBakery.LoadedJson json : jsons) {
            if (!(json.data() instanceof JsonObject obj)) continue;
            JsonObject variants = GsonHelper.getAsJsonObject(obj, "variants", null);
            if (variants == null) continue;

            // Tries to find the matching variant
            JsonObject variantObj = variants.getAsJsonObject(variant);
            if (variantObj == null) continue;

            // Tries to parse the mutations
            JsonObject mutators = GsonHelper.getAsJsonObject(variantObj, "pandora_core:mutators", null);
            if (mutators == null) continue;
            EnumMap<Direction, FaceAdjacency.Mutation> result = new EnumMap<>(Direction.class);
            for (Map.Entry<String, JsonElement> entry : mutators.entrySet()) {
                try {
                    Direction direction = Direction.valueOf(entry.getKey().toUpperCase(Locale.ROOT));
                    FaceAdjacency.Mutation mutation = FaceAdjacency.Mutation.valueOf(entry.getValue().getAsString().toUpperCase(Locale.ROOT));
                    result.put(direction, mutation);
                } catch (IllegalArgumentException ignored) {}
            }
            return result.isEmpty() ? null : result;
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

            // Creates a HolderSet by getting the registry Reference for each block
            if (!uniqueBlocks.isEmpty()) {
                return HolderSet.direct(uniqueBlocks.stream()
                        .map(Block::builtInRegistryHolder)
                        .toList());
            }
        }

        return null;
    }

    /**
     * Checks if a block with the given name exists in the {@code BLOCKS} registry.
     * If the block was found, it ensures the block isn't air, before adding it to the given {@link Set}.
     *
     * @param name         The name of the block (used to create a {@link ResourceLocation}).
     * @param uniqueBlocks A {@link Set}, the block will be added to if found.
     */
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