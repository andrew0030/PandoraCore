package com.github.andrew0030.pandora_core.client.ctm;

import com.github.andrew0030.pandora_core.client.ctm.types.BaseCTMType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Set;

public class CTMDataResolver {
    private BaseCTMType ctmType;
    private HolderSet<Block> connectingBlocks;
    private Set<Property<?>> properties;
    private EnumMap<Direction, FaceAdjacency.Mutation> mutations;
    private boolean checkInFront = true;

    private CTMDataResolver() {}

    /**
     * Creates a new {@link CTMDataResolver} using data from the model's JSON definition.
     *
     * @param modelId the resource location of the block model.
     * @return a configured {@link CTMDataResolver}.
     */
    public static CTMDataResolver from(ResourceLocation modelId) {
        CTMDataResolver dataResolver = new CTMDataResolver();
        dataResolver.ctmType = CTMJsonHelper.getCTMType(modelId);
        dataResolver.connectingBlocks = CTMJsonHelper.getConnectsWith(modelId);
        dataResolver.properties = CTMJsonHelper.getPropertiesToCheck(modelId);
        dataResolver.mutations = CTMJsonHelper.getMutations(modelId);
        dataResolver.checkInFront = CTMJsonHelper.getInnerEdges(modelId);

        return dataResolver;
    }

    /** @return the CTM connection type associated with this model, or {@code null} if none is defined. */
    public @Nullable BaseCTMType getCTMType() {
        return this.ctmType;
    }

    /** @return whether connections should also be considered in front of the faces. */
    public boolean checkInFrontOf() {
        return this.checkInFront;
    }

    /**
     * A more advanced version of {@link BlockState#is}, which takes into account connection logic
     * specified in the block state JSON (such as tags and properties).
     * @param level The {@link BlockAndTintGetter} used to check the target position.
     * @param pos   The {@link BlockPos} of the target block.
     * @param self  The block's own {@link BlockState}.
     * @return Whether the block at the target position is matching the current one.
     */
    public boolean canConnectWith(BlockAndTintGetter level, BlockPos pos, BlockState self) {
        BlockState target = level.getBlockState(pos);

        // We check whether the target is matching
        if (this.connectingBlocks == null) {
            if (!target.is(self.getBlock())) return false;
        } else {
            if (!target.is(this.connectingBlocks)) return false;
        }

        // If there are no properties to compare, the basic block match is enough
        if (this.properties == null) return true;

        // We check if the block states have the properties and if their values match
        for (Property<?> property : this.properties) {
            if (!target.hasProperty(property)) return false;
            if (!Objects.equals(self.getValue(property), target.getValue(property))) return false;
        }

        return true;
    }

    /**
     * Checks whether any face adjacency mutations are defined.
     *
     * @return {@code true} if the mutations map is non-null and contains at least one entry;
     *         {@code false} otherwise.
     */
    public boolean hasMutations() {
        return this.mutations != null && !this.mutations.isEmpty();
    }

    /**
     * Returns the map of directional mutations to apply for face adjacency transformation.
     *
     * @return an {@link EnumMap} of {@link Direction} to {@link FaceAdjacency.Mutation} if defined,
     *         or {@code null} if no mutations are present.
     */
    public @Nullable EnumMap<Direction, FaceAdjacency.Mutation> getMutations() {
        return this.mutations;
    }
}