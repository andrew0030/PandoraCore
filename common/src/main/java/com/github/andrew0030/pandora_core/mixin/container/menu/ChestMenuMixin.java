package com.github.andrew0030.pandora_core.mixin.container.menu;

import com.github.andrew0030.pandora_core.menu.PaCoBuiltInMenuTargets;
import com.github.andrew0030.pandora_core.mixin_interfaces.container.IPaCoExpandedMenuId;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestMenu.class)
public class ChestMenuMixin {

    @Inject(method = "threeRows(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;)Lnet/minecraft/world/inventory/ChestMenu;", at = @At("RETURN"))
    private static void expandedMenuIdThreeRows(int containerId, Inventory playerInventory, Container container, CallbackInfoReturnable<ChestMenu> cir) {
        if (container instanceof PlayerEnderChestContainer) {
            AbstractContainerMenu menu = cir.getReturnValue();
            ((IPaCoExpandedMenuId) menu).pandoraCore$setExpandedMenuId(PaCoBuiltInMenuTargets.ENDER_CHEST);
        }
    }
}