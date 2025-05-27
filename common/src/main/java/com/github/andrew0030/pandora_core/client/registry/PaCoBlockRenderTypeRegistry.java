package com.github.andrew0030.pandora_core.client.registry;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A simple, cross-platform registry helper used for specifying {@link RenderType RenderTypes}
 * for {@link Block} instances, in a unified way for all mod loaders.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * public static final PaCoBlockRenderTypeRegistry RENDER_TYPES = new PaCoBlockRenderTypeRegistry();
 * static {
 *     RENDER_TYPES.add(RenderType.cutout(), ExampleModBlocks.EXAMPLE_BLOCK);
 * }
 * }</pre>
 *
 * Note: check {@link ItemBlockRenderTypes} for reference.
 *
 * <p>And then client side during mod construction:</p>
 * <pre>{@code
 * ExampleModBlockRenderTypes.RENDER_TYPES.register();
 * }</pre>
 */
public class PaCoBlockRenderTypeRegistry {
    private final Map<Supplier<Block>, RenderType> renderTypes = new HashMap<>();

    /**
     * Specifies a {@link RenderType} for one or more {@link Block} instances.
     *
     * @param type   the {@link RenderType} to associate with the given {@link Block} instances.
     * @param blocks one or more {@link Supplier Suppliers} of {@link Block Blocks} to specify the type for.
     */
    @SafeVarargs
    public final void add(RenderType type, Supplier<Block>... blocks) {
        for (Supplier<Block> block : blocks)
            this.renderTypes.put(block, type);
    }

    /**
     * This needs to be called, so event listeners are created by the loaders.<br/>
     * Here is a list of when to call it, on each loader:<br/><br/>
     * <strong>Forge</strong>: Client side, inside mod constructor.<br/>
     * <strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.<br/>
     */
    public void register() {
        Services.REGISTRY.registerBlockRenderTypes(this.renderTypes);
    }
}