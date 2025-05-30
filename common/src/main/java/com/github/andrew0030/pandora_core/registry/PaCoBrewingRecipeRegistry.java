package com.github.andrew0030.pandora_core.registry;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple, cross-platform registry helper used for registering brewing recipes,
 * in a unified way for all mod loaders.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * public static final PaCoBrewingRecipeRegistry BREWING_RECIPES = new PaCoBrewingRecipeRegistry();
 * static {
 *     BREWING_RECIPES.add(Potions.AWKWARD, ExampleModItems.EXAMPLE.get(), ExampleModPotions.EXAMPLE.get());
 * }
 * }</pre>
 *
 * <p>And then during thread-safe init:</p>
 * <pre>{@code
 * ExampleModBrewingRecipes.BREWING_RECIPES.register();
 * }</pre>
 */
public class PaCoBrewingRecipeRegistry {
    private final List<Entry> brewingRecipes = new ArrayList<>();

    /**
     * Registers a brewing recipe.
     *
     * @param input      the input {@link Potion} type.
     * @param ingredient the required {@link Item} to brew the {@link Potion}.
     * @param output     the output {@link Potion} type.
     */
    public void add(Potion input, Item ingredient, Potion output) {
        this.brewingRecipes.add(new Entry(input, ingredient, output));
    }

    /**
     * This needs to be called, so entries are registered by the loaders.<br/>
     * Here is a list of when to call it, on each loader:<br/><br/>
     * <strong>Forge</strong>: Inside {@code FMLCommonSetupEvent} and {@code enqueued}.<br/>
     * <strong>Fabric</strong>: Inside {@code ModInitializer#onInitialize}.<br/>
     */
    public void register() {
        Services.REGISTRY.registerBrewingRecipes(this.brewingRecipes);
    }

    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public record Entry(Potion input, Item ingredient, Potion output) {};
}