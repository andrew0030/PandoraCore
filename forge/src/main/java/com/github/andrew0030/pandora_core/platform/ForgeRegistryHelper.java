package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.client.registry.PaCoParticleProviderRegistry;
import com.github.andrew0030.pandora_core.platform.services.IRegistryHelper;
import com.github.andrew0030.pandora_core.registry.PaCoBrewingRecipeRegistry;
import com.github.andrew0030.pandora_core.registry.PaCoFlammableBlockRegistry;
import com.github.andrew0030.pandora_core.registry.PaCoRegistryBuilder;
import com.github.andrew0030.pandora_core.registry.PaCoRegistryObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ForgeRegistryHelper implements IRegistryHelper {

    @Override
    public <T> void register(Registry<T> registry, String modId, Map<String, PaCoRegistryObject<T>> registryQueue) {
        DeferredRegister<T> deferred = DeferredRegister.create(registry.key(), modId);
        registryQueue.forEach(deferred::register);
        deferred.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Override
    public <T> void registerCustom(PaCoRegistryBuilder.SimpleSpec<T> spec, String modId, Map<String, PaCoRegistryObject<T>> registryQueue) {
        ResourceKey<Registry<T>> resourceKey = spec.getResourceKey();
        DeferredRegister<T> deferred = DeferredRegister.create(resourceKey, modId);
        deferred.makeRegistry(() -> {
            RegistryBuilder<T> builder = new RegistryBuilder<>();
            // Custom registries without tag support don't have an associated vanilla Registry
            // So since I want for there to always be one I will keep this line here unconditionally
            builder.hasTags();
            // If sync hasn't been enabled we disable sync for this registry
            if (!spec.getSync()) builder.disableSync();
            // If saving hasn't been enabled we disable saving for this registry
            if (!spec.getPersistent()) builder.disableSaving();
            // If a default entry has been specified we set it here
            spec.getDefaultId().ifPresent(builder::setDefaultKey);
            // Lastly we return the configured builder
            return builder;
        });
        registryQueue.forEach(deferred::register);
        deferred.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Override
    public <T> void registerDynamic(PaCoRegistryBuilder.DynamicSpec<T> spec) {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ResourceKey<Registry<T>> resourceKey = spec.getResourceKey();
        Codec<T> codec = spec.getCodec();
        modEventBus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
            spec.getNetCodec().ifPresentOrElse(
                    netCodec -> event.dataPackRegistry(resourceKey, codec, netCodec),
                    () -> event.dataPackRegistry(resourceKey, codec)
            );
        });
    }

    @Override
    public void registerKeyMappings(List<Pair<KeyMapping, Runnable>> keyMappings) {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Registers Key Mappings.
        modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
            for (Pair<KeyMapping, Runnable> pair : keyMappings) {
                event.register(pair.getFirst());
            }
        });
        // Handle inputs for all Key Mappings.
        forgeEventBus.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                for (Pair<KeyMapping, Runnable> pair : keyMappings) {
                    while (pair.getFirst().consumeClick()) {
                        pair.getSecond().run();
                    }
                }
            }
        });
    }

    @Override
    public void registerModelLayers(Map<ModelLayerLocation, Supplier<LayerDefinition>> modelLayers) {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registers Model Layers.
        modEventBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) -> {
            modelLayers.forEach(event::registerLayerDefinition);
        });
    }

    @Override
    public void registerFlammableBlocks(Map<Block, PaCoFlammableBlockRegistry.Entry> flammables) {
        FireBlock fireblock = (FireBlock) Blocks.FIRE;
        for (Map.Entry<Block, PaCoFlammableBlockRegistry.Entry> entry : flammables.entrySet()) {
            Block block = entry.getKey();
            int igniteOdds = entry.getValue().igniteOdds();
            int burnOdds = entry.getValue().burnOdds();
            fireblock.setFlammable(block, igniteOdds, burnOdds);
        }
    }

    @Override
    public void registerColorHandlers(Map<Supplier<Block>, BlockColor> blockColors, Map<Supplier<? extends ItemLike>, ItemColor> itemColors) {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registers Block Color Handlers
        modEventBus.addListener((RegisterColorHandlersEvent.Block event) -> {
            for (Map.Entry<Supplier<Block>, BlockColor> entry : blockColors.entrySet())
                event.register(entry.getValue(), entry.getKey().get());
        });
        // Registers ItemLike Color Handlers
        modEventBus.addListener((RegisterColorHandlersEvent.Item event) -> {
            for (Map.Entry<Supplier<? extends ItemLike>, ItemColor> entry : itemColors.entrySet())
                event.register(entry.getValue(), entry.getKey().get());
        });
    }

    @Override
    public void registerBlockRenderTypes(Map<Supplier<Block>, RenderType> renderTypes) {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registers Block Render Types.
        modEventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
            for (Map.Entry<Supplier<Block>, RenderType> entry : renderTypes.entrySet())
                ItemBlockRenderTypes.setRenderLayer(entry.getKey().get(), entry.getValue());
        });
    }

    @Override
    public void registerParticleProviders(Map<ParticleType<?>, ParticleProvider<?>> particleProviders, Map<ParticleType<?>, PaCoParticleProviderRegistry.PendingParticleProvider<?>> pendingParticleProviders) {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registers Particle Providers.
        modEventBus.addListener((RegisterParticleProvidersEvent event) -> {
            for (Map.Entry<ParticleType<?>, ParticleProvider<?>> entry : particleProviders.entrySet())
                ForgeRegistryHelper.registerParticleProvider(event, entry.getKey(), entry.getValue());
            for (Map.Entry<ParticleType<?>, PaCoParticleProviderRegistry.PendingParticleProvider<?>> entry : pendingParticleProviders.entrySet())
                ForgeRegistryHelper.registerPendingParticleProvider(event, entry.getKey(), entry.getValue());
        });
    }

    @SuppressWarnings("unchecked")
    private static <T extends ParticleOptions> void registerParticleProvider(RegisterParticleProvidersEvent event, ParticleType<?> type, ParticleProvider<?> provider) {
        event.registerSpecial((ParticleType<T>) type, (ParticleProvider<T>) provider);
    }

    @SuppressWarnings("unchecked")
    private static <T extends ParticleOptions> void registerPendingParticleProvider(RegisterParticleProvidersEvent event, ParticleType<?> type, PaCoParticleProviderRegistry.PendingParticleProvider<?> pendingProvider) {
        event.registerSpriteSet((ParticleType<T>) type, spriteSet -> (ParticleProvider<T>) pendingProvider.create(spriteSet));
    }

    @Override
    public void registerBrewingRecipes(List<PaCoBrewingRecipeRegistry.Entry> brewingRecipes) {
        // Registers Brewing Recipes.
        for (PaCoBrewingRecipeRegistry.Entry entry : brewingRecipes)
            BrewingRecipeRegistry.addRecipe(new PaCoBrewingRecipe(entry.input(), entry.ingredient(), entry.output()));
    }

    /** A small helper record to make registering Potions easier */
    private record PaCoBrewingRecipe(Potion input, Item ingredient, Potion output) implements IBrewingRecipe {

        @Override
        public boolean isInput(@NotNull ItemStack input) {
            return PotionUtils.getPotion(input) == this.input;
        }

        @Override
        public boolean isIngredient(@NotNull ItemStack ingredient) {
            return ingredient.getItem() == this.ingredient;
        }

        @Override
        public @NotNull ItemStack getOutput(@NotNull ItemStack input, @NotNull ItemStack ingredient) {
            if (!this.isInput(input) || !this.isIngredient(ingredient)) return ItemStack.EMPTY;
            ItemStack itemStack = new ItemStack(input.getItem());
            PotionUtils.setPotion(itemStack, this.output);
            return itemStack;
        }
    }

    @Override
    public void registerEntityAttributes(Map<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier>> entityAttributes) {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registers Entity Attributes.
        modEventBus.addListener((EntityAttributeCreationEvent event) -> {
            for (Map.Entry<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier>> entry : entityAttributes.entrySet())
                event.put(entry.getKey().get(), entry.getValue().get());
        });
    }
}