package com.github.andrew0030.pandora_core.mixin.container;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoCheckInventory;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Inventory.class)
public class InventoryMixin implements IPaCoCheckInventory {

    @Override
    public boolean pandoraCore$isInventory() {
        return true;
    }
}