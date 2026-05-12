package com.github.andrew0030.pandora_core.mixin.container.menu;

import com.github.andrew0030.pandora_core.menu.PaCoBuiltInMenuTargets;
import com.github.andrew0030.pandora_core.mixin_interfaces.container.IPaCoExpandedMenuId;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BarrelBlockEntity.class)
public class BarrelBlockEntityMixin {

    @Inject(method = "createMenu", at = @At("RETURN"))
    public void expandedMenuIdCreateMenu(int containerId, Inventory inventory, CallbackInfoReturnable<AbstractContainerMenu> cir) {
        AbstractContainerMenu menu = cir.getReturnValue();
        ((IPaCoExpandedMenuId) menu).pandoraCore$setExpandedMenuId(PaCoBuiltInMenuTargets.BARREL);
    }
}