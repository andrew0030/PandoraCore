package com.github.andrew0030.pandora_core.mixin.tab;

import com.github.andrew0030.pandora_core.tab.PaCoTabManager;
import com.github.andrew0030.pandora_core.tab.TabInsertion;
import com.github.andrew0030.pandora_core.tab.TabVisibility;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/* For the targets and how to modify tabs with a mixin, I took reference from: */
/* https://github.com/FabricMC/fabric/blob/1.20.1/fabric-item-group-api-v1/src/main/java/net/fabricmc/fabric/mixin/itemgroup/ItemGroupMixin.java */
@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @Shadow private Collection<ItemStack> displayItems;
    @Shadow private Set<ItemStack> displayItemsSearchTab;

    @SuppressWarnings("deprecation")
    @Inject(method = "buildContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;rebuildSearchTree()V"))
    public void injectBuildContents(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci) {
        CreativeModeTab tab = (CreativeModeTab) (Object) this;
        ResourceKey<CreativeModeTab> tabKey = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).orElseThrow(() -> new IllegalStateException("Unregistered creative tab: " + tab));

        // Skips special tabs (except Operator Blocks)
        // Special tabs include: Saved Hotbars, Search, and Survival Inventory.
        // Note: search gets modified as part of the parent tab.
        if (tab.isAlignedRight() && tabKey != CreativeModeTabs.OP_BLOCKS) return;

        List<ItemStack> mutableDisplayItems = new LinkedList<>(this.displayItems);
        List<ItemStack> mutableDisplayItemsSearchTab = new LinkedList<>(this.displayItemsSearchTab);

        for (TabInsertion tabInsertion : PaCoTabManager.getInsertionsFor(tabKey)) {
            if (tabInsertion.getVisibility() != TabVisibility.SEARCH_TAB_ONLY)
                tabInsertion.getAction().apply(mutableDisplayItems);
            if (tabInsertion.getVisibility() != TabVisibility.PARENT_TAB_ONLY)
                tabInsertion.getAction().apply(mutableDisplayItemsSearchTab);
        }

        this.displayItems.clear();
        this.displayItems.addAll(mutableDisplayItems);
        this.displayItemsSearchTab.clear();
        this.displayItemsSearchTab.addAll(mutableDisplayItemsSearchTab);
    }
}