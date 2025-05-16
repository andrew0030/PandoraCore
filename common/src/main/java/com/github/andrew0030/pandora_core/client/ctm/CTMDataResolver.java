package com.github.andrew0030.pandora_core.client.ctm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Objects;
import java.util.Set;

//TODO The things "CTMDataResolver" should be able to do:
// - Parse "connects" or smt like that, it should allow blocks/tags to be used to specify which blocks this one connects with.
// - Parse "state" or smt like that, which should allow specifying which state properties need to match e.g. "facing" or "direction".
// Internally this will require some sort of [String -> Property] logic? Maybe do this with a function so adding new properties to the list is easy?
// - Parse "mutators" or something like that, this will be (optionally) specified per block variant, allowing to "mutate" block face connection detection.
// - Maybe deal with CTM types inside this class?

public class CTMDataResolver {
    private HolderSet<Block> connectingBlocks;
    private Set<Property<?>> properties;

    private CTMDataResolver() {}

    public static CTMDataResolver from(ResourceLocation modelId) {
        CTMDataResolver dataResolver = new CTMDataResolver();
        dataResolver.connectingBlocks = CTMJsonHelper.getConnectsWith(modelId);
        dataResolver.properties = CTMJsonHelper.getPropertiesToCheck(modelId);

        return dataResolver;
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
            if (!self.hasProperty(property) || !target.hasProperty(property)) return false;
            if (!Objects.equals(self.getValue(property), target.getValue(property))) return false;
        }

        return true;
    }
}