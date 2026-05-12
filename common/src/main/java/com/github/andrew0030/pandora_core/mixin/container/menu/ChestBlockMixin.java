package com.github.andrew0030.pandora_core.mixin.container.menu;

import com.github.andrew0030.pandora_core.menu.PaCoBuiltInMenuTargets;
import com.github.andrew0030.pandora_core.mixin_interfaces.container.IPaCoExpandedMenuId;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class ChestBlockMixin {

    @Inject(method = "getMenuProvider", at = @At("RETURN"), cancellable = true)
    public void expandedMenuIdGetMenuProvider(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<MenuProvider> cir) {
        MenuProvider original = cir.getReturnValue();
        if (original == null) return;
        cir.setReturnValue(new MenuProvider() {
            @NotNull
            @Override
            public Component getDisplayName() {
                return original.getDisplayName();
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
                AbstractContainerMenu menu = original.createMenu(containerId, playerInventory, player);
                if (menu != null) {
                    boolean isTrapped = state.getBlock() instanceof TrappedChestBlock;
                    boolean isSingle = state.getValue(ChestBlock.TYPE).equals(ChestType.SINGLE);
                    ResourceLocation menuId;
                    if (isSingle) {
                        menuId = isTrapped ? PaCoBuiltInMenuTargets.TRAPPED_CHEST : PaCoBuiltInMenuTargets.CHEST;
                    } else {
                        menuId = isTrapped ? PaCoBuiltInMenuTargets.DOUBLE_TRAPPED_CHEST : PaCoBuiltInMenuTargets.DOUBLE_CHEST;
                    }
                    ((IPaCoExpandedMenuId) menu).pandoraCore$setExpandedMenuId(menuId);
                }
                return menu;
            }
        });
    }
}