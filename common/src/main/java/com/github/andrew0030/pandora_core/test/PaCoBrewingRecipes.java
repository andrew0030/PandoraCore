package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.registry.PaCoBrewingRecipeRegistry;
import com.github.andrew0030.pandora_core.registry.test.PaCoItems;
import net.minecraft.world.item.alchemy.Potions;

public class PaCoBrewingRecipes {
    public static final PaCoBrewingRecipeRegistry BREWING_RECIPES = new PaCoBrewingRecipeRegistry();

    static {
        BREWING_RECIPES.add(Potions.AWKWARD, PaCoItems.FUNK.get(), Potions.STRONG_LEAPING);
    }
}