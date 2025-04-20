package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.client.render.renderers.registry.InstancedBERendererRegistry;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Mod(PandoraCore.MOD_ID)
public class PandoraCoreForge {
    static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "a");
    static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "a");
    static final RegistryObject<SBEB> SBE_BLOCK = BLOCKS.register("a", () -> new SBEB(BlockBehaviour.Properties.copy(Blocks.STONE)));
    static final RegistryObject<BlockEntityType<SBE>> SBE_TYPE = BLOCK_ENTITY_TYPES.register("a", () -> new BlockEntityType<>(SBE::new, Sets.newHashSet(SBE_BLOCK.get()), null));

    public PandoraCoreForge() {
        PandoraCore.earlyInit();
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::commonSetup);
        if (FMLEnvironment.dist == Dist.CLIENT)
            PandoraCoreClientForge.init(modEventBus, forgeEventBus);

        BLOCKS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener((Consumer<? extends FMLClientSetupEvent>) (event) -> {
            event.enqueueWork(() -> {
                InstancedBERendererRegistry.register(
                        SBE_TYPE.get(),
                        new SBERenderer()
                );
            });
        });
    }

    static class SBEB extends BaseEntityBlock {
        public SBEB(Properties pProperties) {
            super(pProperties);
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
            return new SBE(pPos, pState);
        }

        @Override
        public RenderShape getRenderShape(BlockState pState) {
            return RenderShape.ENTITYBLOCK_ANIMATED;
        }
    }

    static class SBE extends BlockEntity {
        public SBE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
            super(pType, pPos, pBlockState);
        }

        public SBE(BlockPos pos, BlockState state) {
            super(SBE_TYPE.get(), pos, state);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Common Module Initialization.
        PandoraCore.init();
        event.enqueueWork(PandoraCore::initThreadSafe);

        // Loader Module Initialization.
        // Nothing atm...
    }
}