package com.github.andrew0030.pandora_core.mixin.tab;

import com.github.andrew0030.pandora_core.registry.PaCoBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

/* For the targets and how to modify tabs with a mixin, I took reference from: */
/* https://github.com/FabricMC/fabric/blob/1.20.1/fabric-item-group-api-v1/src/main/java/net/fabricmc/fabric/mixin/itemgroup/ItemGroupMixin.java */
@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @Shadow private Collection<ItemStack> displayItems;
    @Shadow private Set<ItemStack> displayItemsSearchTab;

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

        if (tabKey == CreativeModeTabs.REDSTONE_BLOCKS) {

            addAfter(mutableDisplayItems, new ItemStack(Items.REDSTONE), new ItemStack(PaCoBlocks.TEST.get()));
            addAfter(mutableDisplayItemsSearchTab, new ItemStack(Items.REDSTONE), new ItemStack(PaCoBlocks.TEST.get()));

            this.displayItems.clear();
            this.displayItems.addAll(mutableDisplayItems);
            this.displayItemsSearchTab.clear();
            this.displayItemsSearchTab.addAll(mutableDisplayItemsSearchTab);
        }
    }

    @Unique
    private void addAfter(List<ItemStack> list, ItemStack target, ItemStack toInsert) {
        ListIterator<ItemStack> iterator = list.listIterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            if (stack.is(target.getItem())) {
                iterator.add(toInsert);
                return;
            }
        }
        // Fallback if the target is not found
        list.add(toInsert);
    }
}