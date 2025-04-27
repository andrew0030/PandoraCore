package com.github.andrew0030.pandora_core.registry;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.tab.PaCoTabManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
        })
        .build()
    );

    public static void insertItems() {
        // Target stack with NBT data
        ItemStack target = new ItemStack(Items.PAINTING);
        target.getOrCreateTagElement("EntityTag").putString("variant", "minecraft:aztec");
        PaCoTabManager.insertionBuilder(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .add(PaCoItems.FUNK.get())
                .insertAfter(target)
                .apply();

        // Anti infinite loop check
        PaCoTabManager.insertionBuilder(CreativeModeTabs.REDSTONE_BLOCKS)
                .add(PaCoBlocks.INSTANCING_TEST.get())
                .insertBefore(PaCoBlocks.TEST.get())
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.REDSTONE_BLOCKS)
                .add(PaCoItems.FUNK.get())
                .insertAfter(PaCoBlocks.TEST.get())
                .apply();
        PaCoTabManager.insertionBuilder(CreativeModeTabs.REDSTONE_BLOCKS)
                .add(PaCoBlocks.TEST.get())
                .insertBefore(PaCoItems.FUNK.get())
                .apply();

        // 10.000 stacks insertion
//        ItemStack baseStack = new ItemStack(PaCoItems.FUNK.get());
//        List<ItemStack> items = new ArrayList<>();
//        for (int i = 0; i < 10000; i++) {
//            ItemStack stack = baseStack.copy();
//            stack.getOrCreateTag().putInt("value", i);
//            items.add(stack);
//        }
//        ItemStack[] stacks = items.toArray(ItemStack[]::new);
//        PaCoTabManager.insertionBuilder(CreativeModeTabs.REDSTONE_BLOCKS)
//                .add(stacks)
//                .insertAfter(Items.REDSTONE_TORCH)
//                .apply();
    }
}