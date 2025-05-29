package com.github.andrew0030.pandora_core.blocks;

import com.github.andrew0030.pandora_core.block_entities.TestBlockEntity;
import com.github.andrew0030.pandora_core.test.particle.PaCoParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class TestBlock extends BaseEntityBlock {

    public TestBlock() {
        super(TestBlock.getProperties());
    }

    private static Properties getProperties() {
        Properties properties = Block.Properties.of().mapColor(MapColor.WOOL);
        properties.strength(1.5F, 6.0F);
        properties.noOcclusion();
        return properties;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TestBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult result) {
        Random random = new Random();
        if (level.isClientSide()) {
            for (int i = 0; i < 10; i++)
                level.addParticle(PaCoParticles.STAR_TEST.get(), pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 1 * (-0.5 + random.nextFloat()), 1 * (-0.5 + random.nextFloat()), 1 * (-0.5 + random.nextFloat()));
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, result);
    }
}