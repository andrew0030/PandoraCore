package com.github.andrew0030.pandora_core.registry;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple, cross-platform registry helper used to make {@link Block Blocks} flammable.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * public static final PaCoFlammableBlockRegistry FLAMMABLE_BLOCKS = new PaCoFlammableBlockRegistry();
 * static {
 *     FLAMMABLE_BLOCKS.add(ExampleModBlocks.EXAMPLE.get(), 5, 20);
 * }
 * }</pre>
 *
 * Note: check {@link FireBlock#setFlammable} for reference on {@code igniteOdds} and {@code burnOdds}.
 *
 * <p>And then during thread-safe init:</p>
 * <pre>{@code
 * ExampleModFlammables.FLAMMABLE_BLOCKS.register();
 * }</pre>
 */
public class PaCoFlammableBlockRegistry {
    private final Map<Block, Entry> flammables = new HashMap<>();

    /**
     * Adds a {@link Block} and its corresponding {@code igniteOdds} and {@code burnOdds} to be registered later.
     *
     * @param igniteOdds The ignition odds of the given block.
     * @param burnOdds   The burn odds of the given block.
     */
    public void add(Block block, int igniteOdds, int burnOdds) {
        this.flammables.put(block, new Entry(igniteOdds, burnOdds));
    }

    /**
     * Adds the {@link Block Blocks} of a given {@link TagKey} and their corresponding
     * {@code igniteOdds} and {@code burnOdds} to be registered later.
     *
     * @param igniteOdds The ignition odds of the given blocks.
     * @param burnOdds   The burn odds of the given blocks.
     */
    public void add(TagKey<Block> blockTag, int igniteOdds, int burnOdds) {
        Entry entry = new Entry(igniteOdds, burnOdds);
        for (Holder<Block> block : BuiltInRegistries.BLOCK.getTagOrEmpty(blockTag))
            this.flammables.put(block.value(), entry);
    }

    /**
     * This needs to be called, so entries are registered by the loaders.<br/>
     * Here is a list of when to call it, on each loader:<br/><br/>
     * <strong>Forge</strong>: Inside {@code FMLCommonSetupEvent} and {@code enqueued}.<br/>
     * <strong>Fabric</strong>: Inside {@code ModInitializer#onInitialize}.<br/>
     */
    public void register() {
        Services.REGISTRY.registerFlammableBlocks(this.flammables);
    }

    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public record Entry(int igniteOdds, int burnOdds) {};
}