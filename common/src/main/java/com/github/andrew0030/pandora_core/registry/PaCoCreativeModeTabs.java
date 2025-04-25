package com.github.andrew0030.pandora_core.registry;

import com.github.andrew0030.pandora_core.PandoraCore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class PaCoCreativeModeTabs {
    public static final PaCoRegistry<CreativeModeTab> CREATIVE_MODE_TABS = new PaCoRegistry<>(BuiltInRegistries.CREATIVE_MODE_TAB, PandoraCore.MOD_ID);

    public static final Supplier<CreativeModeTab> TAB = CREATIVE_MODE_TABS.add("tab", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
        .title(Component.translatable("itemGroup." + PandoraCore.MOD_ID + ".tab"))
        .icon(() -> new ItemStack(PaCoBlocks.TEST.get()))
        .displayItems((parameters, output) -> {
            output.accept(PaCoItems.FUNK.get());
            output.accept(PaCoBlocks.TEST.get());
            output.accept(PaCoBlocks.INSTANCING_TEST.get());
        })
        .build()
    );
}