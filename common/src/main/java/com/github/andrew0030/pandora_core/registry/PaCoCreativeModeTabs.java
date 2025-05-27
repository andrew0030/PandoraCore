package com.github.andrew0030.pandora_core.registry;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.tab.PaCoTabManager;
import com.github.andrew0030.pandora_core.tab.TabVisibility;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public class PaCoCreativeModeTabs {
    public static final PaCoRegistry<CreativeModeTab> CREATIVE_MODE_TABS = new PaCoRegistry<>(BuiltInRegistries.CREATIVE_MODE_TAB, PandoraCore.MOD_ID);

    public static final Supplier<CreativeModeTab> TAB = CREATIVE_MODE_TABS.add("tab", () -> PaCoTabManager.builder()
        .title(Component.translatable("itemGroup." + PandoraCore.MOD_ID + ".tab"))
        .icon(() -> new ItemStack(PaCoBlocks.TEST.get()))
        .displayItems((parameters, output) -> {
            output.accept(PaCoItems.FUNK.get());
            output.accept(PaCoBlocks.TEST.get());
            output.accept(PaCoBlocks.INSTANCING_TEST.get());
            output.accept(PaCoBlocks.CONNECTED_BLOCK.get());
            output.accept(PaCoBlocks.CTM_HORIZONTAL.get());
            output.accept(PaCoBlocks.CTM_VERTICAL.get());
            output.accept(PaCoBlocks.CTM_RANDOM.get());
            output.accept(PaCoBlocks.CTM_REPEAT.get());
            output.accept(PaCoBlocks.FOLIAGE_TEST.get());
        })
        .build()
    );

    public static void insertItems() {
        // Target stack with NBT data
        ItemStack target = new ItemStack(Items.PAINTING);
        target.getOrCreateTagElement("EntityTag").putString("variant", "minecraft:aztec");
        PaCoTabManager.insertionBuilder(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .add(Items.GREEN_DYE)
                .insertBefore(target)
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .add(Items.YELLOW_DYE)
                .insertBefore(target)
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .add(Items.RED_DYE)
                .insertBefore(target)
                .apply();

        // Before and after check
        PaCoTabManager.insertionBuilder(CreativeModeTabs.INGREDIENTS)
                .add(PaCoBlocks.INSTANCING_TEST.get())
                .insertAfter(Items.ENCHANTED_BOOK)
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.INGREDIENTS)
                .add(PaCoBlocks.TEST.get())
                .insertBefore(Items.ENCHANTED_BOOK)
                .apply();

        // Missing target stack
        PaCoTabManager.insertionBuilder(CreativeModeTabs.NATURAL_BLOCKS)
                .add(PaCoBlocks.TEST.get())
                .insertBefore(Blocks.REDSTONE_TORCH)
                .apply();

        // Anti infinite loop check
        PaCoTabManager.insertionBuilder(CreativeModeTabs.REDSTONE_BLOCKS)
                .add(PaCoItems.FUNK.get())
                .insertAfter(PaCoBlocks.TEST.get())
                .targetsInsertion()
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.REDSTONE_BLOCKS)
                .add(PaCoBlocks.INSTANCING_TEST.get())
                .insertAfter(PaCoBlocks.TEST.get())
                .targetsInsertion()
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.REDSTONE_BLOCKS)
                .add(PaCoBlocks.TEST.get())
                .insertBefore(PaCoItems.FUNK.get())
                .targetsInsertion()
                .apply();

        // 10.000 stacks in 1 insertion
//        ItemStack baseStack = new ItemStack(PaCoItems.FUNK.get());
//        List<ItemStack> items = new ArrayList<>();
//        for (int i = 0; i < 10000; i++) {
//            ItemStack stack = baseStack.copy();
//            stack.getOrCreateTag().putInt("value", i);
//            items.add(stack);
//        }
//        ItemStack[] stacks = items.toArray(ItemStack[]::new);
//        PaCoTabManager.insertionBuilder(CreativeModeTabs.FUNCTIONAL_BLOCKS)
//                .add(stacks)
//                .insertAfter(Items.PAINTING)
//                .apply();

        // 10.000 stacks in 10.000 insertion
//        ItemStack baseStack = new ItemStack(PaCoItems.FUNK.get());
//        for (int i = 0; i < 10000; i++) {
//            ItemStack stack = baseStack.copy();
//            stack.getOrCreateTag().putInt("value", i);
//            PaCoTabManager.insertionBuilder(CreativeModeTabs.FUNCTIONAL_BLOCKS)
//                    .add(stack)
//                    .insertBefore(Items.PAINTING)
//                    .apply();
//        }

        // Visibility tests
        PaCoTabManager.insertionBuilder(CreativeModeTabs.BUILDING_BLOCKS)
                .add(PaCoBlocks.TEST.get())
                .insertBefore(Blocks.OAK_DOOR)
                .visibility(TabVisibility.PARENT_TAB_ONLY)
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.BUILDING_BLOCKS)
                .add(PaCoBlocks.INSTANCING_TEST.get())
                .insertAfter(Blocks.OAK_DOOR)
                .visibility(TabVisibility.SEARCH_TAB_ONLY)
                .apply();

        // Insert existing item test
        PaCoTabManager.insertionBuilder(CreativeModeTabs.COLORED_BLOCKS)
                .add(Items.CANDLE)
                .insertBefore(Blocks.YELLOW_BED)
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.COLORED_BLOCKS)
                .add(Items.CARROT)
                .insertAfter(Items.CANDLE)
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.COLORED_BLOCKS)
                .add(Items.APPLE, Items.COOKIE)
                .insertBefore(Items.CANDLE)
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.COLORED_BLOCKS)
                .add(Items.MELON_SLICE)
                .insertBefore(Blocks.LIME_BED)
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.COLORED_BLOCKS)
                .add(Items.GOLDEN_APPLE)
                .insertAfter(Items.ORANGE_CANDLE)
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.COLORED_BLOCKS)
                .add(Items.GOLDEN_CARROT)
                .insertAfter(Items.CANDLE)
                .apply();

        // Inserting same item twice
        PaCoTabManager.insertionBuilder(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .add(PaCoBlocks.TEST.get())
                .insertAfter(Blocks.DRAGON_EGG)
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .add(PaCoBlocks.TEST.get())
                .insertAfter(Items.ENDER_EYE)
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .add(PaCoItems.FUNK.get())
                .insertAfter(PaCoBlocks.TEST.get())
                .apply();

        // Self insertion
        PaCoTabManager.insertionBuilder(CreativeModeTabs.FOOD_AND_DRINKS)
                .add(PaCoItems.FUNK.get())
                .insertAfter(PaCoItems.FUNK.get())
                .targetsInsertion()
                .apply();
    }
}