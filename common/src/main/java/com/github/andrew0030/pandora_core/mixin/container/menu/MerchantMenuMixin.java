package com.github.andrew0030.pandora_core.mixin.container.menu;

import com.github.andrew0030.pandora_core.menu.PaCoBuiltInMenuTargets;
import com.github.andrew0030.pandora_core.mixin_interfaces.container.IPaCoExpandedMenuId;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(MerchantMenu.class)
public class MerchantMenuMixin implements IPaCoExpandedMenuId {
    @Unique private static final Map<VillagerProfession, ResourceLocation> PROFESSION_TO_MENU = Map.ofEntries(
            Map.entry(VillagerProfession.ARMORER, PaCoBuiltInMenuTargets.VILLAGER_ARMORER),
            Map.entry(VillagerProfession.BUTCHER, PaCoBuiltInMenuTargets.VILLAGER_BUTCHER),
            Map.entry(VillagerProfession.CARTOGRAPHER, PaCoBuiltInMenuTargets.VILLAGER_CARTOGRAPHER),
            Map.entry(VillagerProfession.CLERIC, PaCoBuiltInMenuTargets.VILLAGER_CLERIC),
            Map.entry(VillagerProfession.FARMER, PaCoBuiltInMenuTargets.VILLAGER_FARMER),
            Map.entry(VillagerProfession.FISHERMAN, PaCoBuiltInMenuTargets.VILLAGER_FISHERMAN),
            Map.entry(VillagerProfession.FLETCHER, PaCoBuiltInMenuTargets.VILLAGER_FLETCHER),
            Map.entry(VillagerProfession.LEATHERWORKER, PaCoBuiltInMenuTargets.VILLAGER_LEATHERWORKER),
            Map.entry(VillagerProfession.LIBRARIAN, PaCoBuiltInMenuTargets.VILLAGER_LIBRARIAN),
            Map.entry(VillagerProfession.MASON, PaCoBuiltInMenuTargets.VILLAGER_MASON),
            Map.entry(VillagerProfession.SHEPHERD, PaCoBuiltInMenuTargets.VILLAGER_SHEPHERD),
            Map.entry(VillagerProfession.TOOLSMITH, PaCoBuiltInMenuTargets.VILLAGER_TOOLSMITH),
            Map.entry(VillagerProfession.WEAPONSMITH, PaCoBuiltInMenuTargets.VILLAGER_WEAPONSMITH)
    );

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V", at = @At("TAIL"))
    public void expandedMenuIdInit(int containerId, Inventory playerInventory, Merchant trader, CallbackInfo ci) {
        if (trader instanceof WanderingTrader) {
            this.pandoraCore$setExpandedMenuId(PaCoBuiltInMenuTargets.WANDERING_TRADER);
        } else if (trader instanceof Villager villager) {
            ResourceLocation profession = PROFESSION_TO_MENU.get(villager.getVillagerData().getProfession());
            if (profession != null) {
                this.pandoraCore$setExpandedMenuId(profession);
            }
        }
    }
}