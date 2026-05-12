package com.github.andrew0030.pandora_core.mixin.container.menu;

import com.github.andrew0030.pandora_core.menu.PaCoBuiltInMenuTargets;
import com.github.andrew0030.pandora_core.mixin_interfaces.container.IPaCoExpandedMenuId;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseInventoryMenu.class)
public class HorseInventoryMenuMixin implements IPaCoExpandedMenuId {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void expandedMenuIdInit(int containerId, Inventory playerInventory, Container container, final AbstractHorse horse, CallbackInfo ci) {
        if (horse instanceof Donkey) {
            this.pandoraCore$setExpandedMenuId(PaCoBuiltInMenuTargets.DONKEY);
        } else if (horse instanceof Mule) {
            this.pandoraCore$setExpandedMenuId(PaCoBuiltInMenuTargets.MULE);
        } else if (horse instanceof Horse) {
            this.pandoraCore$setExpandedMenuId(PaCoBuiltInMenuTargets.HORSE);
        }
    }
}