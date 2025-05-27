package com.github.andrew0030.pandora_core.client.registry;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A simple, cross-platform registry helper used for registering {@link Block} and {@link ItemLike}
 * tint colors, in a unified way for all mod loaders.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * public static final PaCoColorHandlerRegistry COLOR_HANDLERS = new PaCoColorHandlerRegistry();
 * static {
 *     // Blocks
 *     COLOR_HANDLERS.addBlock(
 *         (state, level, pos, tintIndex) -> (level != null && pos != null) ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor(),
 *         ExampleModBlocks.EXAMPLE_BLOCK
 *     );
 *     // Items
 *     COLOR_HANDLERS.addItem(
 *         (stack, tintIndex) -> FoliageColor.getDefaultColor(),
 *         ExampleModBlocks.EXAMPLE_BLOCK
 *     );
 * }
 * }</pre>
 *
 * Note: check {@link BlockColors} and {@link ItemColors} for reference.
 *
 * <p>And then client side during mod construction:</p>
 * <pre>{@code
 * ExampleModColorHandlers.COLOR_HANDLERS.register();
 * }</pre>
 */
public class PaCoColorHandlerRegistry {
    private final Map<Supplier<Block>, BlockColor> blockColors = new HashMap<>();
    private final Map<Supplier<? extends ItemLike>, ItemColor> itemColors = new HashMap<>();

    /**
     * Registers a {@link Block} color handler for one or more {@link Block} instances.
     *
     * @param blockColor the {@link BlockColor} implementation to associate with the given {@link Block} instances.
     * @param blocks     one or more {@link Supplier Suppliers} of {@link Block Blocks} to register the color handler for.
     */
    @SafeVarargs
    public final void addBlock(BlockColor blockColor, Supplier<Block>... blocks) {
        for (Supplier<Block> block : blocks)
            this.blockColors.put(block, blockColor);
    }

    /**
     * Registers an {@link ItemLike} color handler for one or more {@link ItemLike} instances.
     *
     * @param itemColor the {@link ItemColor} implementation to associate with the given {@link ItemLike} instances.
     * @param items     one or more {@link Supplier Suppliers} of {@link ItemLike} objects to register the color handler for.
     */
    @SafeVarargs
    public final void addItem(ItemColor itemColor, Supplier<? extends ItemLike>... items) {
        for (Supplier<? extends ItemLike> item : items)
            this.itemColors.put(item, itemColor);
    }

    /**
     * This needs to be called, so event listeners are created by the loaders.<br/>
     * Here is a list of when to call it, on each loader:<br/><br/>
     * <strong>Forge</strong>: Client side, inside mod constructor.<br/>
     * <strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.<br/>
     */
    public void register() {
        Services.REGISTRY.registerColorHandlers(this.blockColors, this.itemColors);
    }
}