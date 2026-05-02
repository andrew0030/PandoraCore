package com.github.andrew0030.pandora_core.mixin.advancement;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoCheckInventory;
import com.github.andrew0030.pandora_core.registry.internal.PaCoCriteriaTriggers;
import com.github.andrew0030.pandora_core.utils.SideChecker;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Shadow @Final public NonNullList<Slot> slots;
    @Shadow @Final @Nullable private MenuType<?> menuType;
    @Shadow @Final private Set<Slot> quickcraftSlots;
    @Shadow public abstract ItemStack getCarried();
    @Unique private final Set<Integer> pandoraCore$targetIndexCache = new HashSet<>();
    @Unique private ItemStack pandoraCore$carriedStackCache = ItemStack.EMPTY;
    @Unique private boolean pandoraCore$isTrackingQuickMove = false;
    @Unique private Slot pandoraCore$localSlot;

    @Inject(method = "clicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;doClick(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V"))
    public void injectBeforeDoClick(int slotId, int button, ClickType type, Player player, CallbackInfo ci) {
        // We can't properly tell what menu is open without it having a type, thus menus without type are ignored
        if (this.menuType == null) return;
        // We only want to trigger an advancement on stack insertion, meaning we only need server
        if (!SideChecker.isServerPlayer(player)) return;
        // Cleanup, just in case to avoid state leaking if an exception occurred
        this.pandoraCore$carriedStackCache = ItemStack.EMPTY;
        this.pandoraCore$isTrackingQuickMove = false;
        this.pandoraCore$targetIndexCache.clear();
        this.pandoraCore$localSlot = null;
        // When the phase is "HEADER_END", items have been added to the "quickcraftSlots" Set.
        // So we cache their indexes, before letting Minecraft handle insertion and Set clearing.
        if (type == ClickType.QUICK_CRAFT && (button & 3) == AbstractContainerMenu.QUICKCRAFT_HEADER_END) {
            for (Slot slot : this.quickcraftSlots) {
                if (((IPaCoCheckInventory) slot.container).pandoraCore$isInventory()) continue;
                int index = slot.index;
                if (index < 0 || index >= this.slots.size()) continue;
                this.pandoraCore$targetIndexCache.add(slot.index);
            }
        }
        // If the type is QUICK_MOVE we set the tracking flag to true so the indexes are captured inside "moveItemStackTo"
        else if (type == ClickType.QUICK_MOVE) {
            this.pandoraCore$isTrackingQuickMove = true;
        }
        // If the type is PICKUP we cache the carried stack before "doClick" mutates it
        else if (type == ClickType.PICKUP) {
            ItemStack carried = this.getCarried();
            this.pandoraCore$carriedStackCache = carried != null ? carried.copy() : ItemStack.EMPTY;
        }
    }

    @Inject(method = "clicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;doClick(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V", shift = At.Shift.AFTER))
    public void injectAfterDoClick(int slotId, int button, ClickType type, Player player, CallbackInfo ci) {
        // We cant properly tell what menu is open without it having a type, thus menus without type are ignored
        if (this.menuType == null) return;
        // We only want to trigger an advancement on stack insertion, meaning we only need server
        if (!SideChecker.isServerPlayer(player)) return;
        ServerPlayer serverPlayer = (ServerPlayer) player;

        try {
            // --- QUICK_CRAFT ---
            // We only care about the "HEADER_END" phase (last one), as at this point items have been cached
            // by the mixin above, and the "doClick" method has finished running the insertion logic.
            if (type == ClickType.QUICK_CRAFT && (button & 3) == AbstractContainerMenu.QUICKCRAFT_HEADER_END) {
                // Checks what successfully ended up in our slots
                if (!this.pandoraCore$targetIndexCache.isEmpty()) {
                    List<ItemStack> itemStacks = new ArrayList<>(this.pandoraCore$targetIndexCache.size());
                    for (int idx : this.pandoraCore$targetIndexCache)
                        itemStacks.add(this.slots.get(idx).getItem());
                    PaCoCriteriaTriggers.ITEM_PLACED_IN_CONTAINER.trigger(serverPlayer, this.menuType, itemStacks);
                }
            }
            // --- QUICK_MOVE ---
            // At this point the affected slots should have been cached by "moveItemStackTo" so we simply use the cache
            else if (type == ClickType.QUICK_MOVE) {
                // Checks what successfully ended up in our slots
                if (!this.pandoraCore$targetIndexCache.isEmpty()) {
                    List<ItemStack> itemStacks = new ArrayList<>(this.pandoraCore$targetIndexCache.size());
                    for (int idx : this.pandoraCore$targetIndexCache)
                        itemStacks.add(this.slots.get(idx).getItem());
                    PaCoCriteriaTriggers.ITEM_PLACED_IN_CONTAINER.trigger(serverPlayer, this.menuType, itemStacks);
                }
            }
            // --- PICKUP ---
            // This gets triggered every time a stack is simply left-clicked, this includes to pickup or to place down.
            // Since we only care about insertion, and this gets also triggered for placement, we have to check if the
            // clicked slot is not an inventory slot, and if there was a carried item before the interaction.
            else if (type == ClickType.PICKUP) {
                if (slotId < 0 || slotId >= this.slots.size()) return;
                // Checks what successfully ended up in our slots
                Slot slot = this.slots.get(slotId);
                if (((IPaCoCheckInventory) slot.container).pandoraCore$isInventory()) return;
                if (this.pandoraCore$carriedStackCache.isEmpty()) return;
                PaCoCriteriaTriggers.ITEM_PLACED_IN_CONTAINER.trigger(serverPlayer, this.menuType, List.of(slot.getItem()));
            }
            // --- SWAP ---
            // Gets triggered when items are swapped e.g. the player uses hotkeys to swap a hotbar item and the hovered item.
            // Since this type uses the id of the slot that was swapped "into", we can mostly reuse the "PICKUP" logic.
            else if (type == ClickType.SWAP) {
                if (slotId < 0 || slotId >= this.slots.size()) return;
                // Checks what successfully ended up in our slots
                Slot slot = this.slots.get(slotId);
                if (((IPaCoCheckInventory) slot.container).pandoraCore$isInventory()) return;
                PaCoCriteriaTriggers.ITEM_PLACED_IN_CONTAINER.trigger(serverPlayer, this.menuType, List.of(slot.getItem()));
            }
        } finally {
            // Cleanup, just in case to avoid state leaking if an exception occurred
            this.pandoraCore$carriedStackCache = ItemStack.EMPTY;
            this.pandoraCore$isTrackingQuickMove = false;
            this.pandoraCore$targetIndexCache.clear();
            this.pandoraCore$localSlot = null;
        }
    }

    // ##################################################################################################################
    // #  Bellow this line is pain and suffering... If you read this, know I am not proud of what had to be done.       #
    // #  That said on a more technical note: Basically instead of modifying "Slot" and doing some hacky index          #
    // #  caching that way, this captures the local variable before any logic has occurred, and then (if move was       #
    // #  successful) it uses the captured "Slot" to get the target index of a slot (which was successfully modified).  #
    // #  Notes:                                                                                                        #
    // #    - Uses "require = 0" to avoid hard breaking if obfuscation shifts locals.                                   #
    // #    - These mixins run in-between the "injectBeforeDoClick" and the "injectAfterDoClick" mixins above.          #
    // ##################################################################################################################

    // Handles caching the first slot local variable
    @ModifyVariable(method = "moveItemStackTo", at = @At(value = "STORE", ordinal = 0), require = 0)
    public Slot injectOnSlotStore(Slot slot) {
        if (this.pandoraCore$isTrackingQuickMove)
            this.pandoraCore$localSlot = slot;
        return slot;
    }

    // Handles caching the second slot local variable
    @ModifyVariable(method = "moveItemStackTo", at = @At(value = "STORE", ordinal = 1), require = 0)
    public Slot injectOnSlotStore1(Slot slot) {
        if (this.pandoraCore$isTrackingQuickMove)
            this.pandoraCore$localSlot = slot;
        return slot;
    }

    // Handles caching the target index if the moved stack was fully consumed by the target
    @Inject(method = "moveItemStackTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;setChanged()V", ordinal = 0, shift = At.Shift.AFTER), require = 0)
    public void injectAfterStackMergeExact(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection, CallbackInfoReturnable<Boolean> cir) {
        if (this.pandoraCore$isTrackingQuickMove && this.pandoraCore$localSlot != null) {
            if (((IPaCoCheckInventory) this.pandoraCore$localSlot.container).pandoraCore$isInventory()) return;
            int index = this.pandoraCore$localSlot.index;
            if (index < 0 || index >= this.slots.size()) return;
            this.pandoraCore$targetIndexCache.add(index);
        }
    }

    // Handles caching the target index if the moved stack fully filled the stack at the target index and there are left over items
    @Inject(method = "moveItemStackTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;setChanged()V", ordinal = 1, shift = At.Shift.AFTER), require = 0)
    public void injectAfterStackFillToMax(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection, CallbackInfoReturnable<Boolean> cir) {
        if (this.pandoraCore$isTrackingQuickMove && this.pandoraCore$localSlot != null) {
            if (((IPaCoCheckInventory) this.pandoraCore$localSlot.container).pandoraCore$isInventory()) return;
            int index = this.pandoraCore$localSlot.index;
            if (index < 0 || index >= this.slots.size()) return;
            this.pandoraCore$targetIndexCache.add(index);
        }
    }

    // Handles caching the target index if the moved stack was moved to an empty slot
    @Inject(method = "moveItemStackTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;setChanged()V", ordinal = 2, shift = At.Shift.AFTER), require = 0)
    public void injectAfterPlaceIntoEmptySlot(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection, CallbackInfoReturnable<Boolean> cir) {
        if (this.pandoraCore$isTrackingQuickMove && this.pandoraCore$localSlot != null) {
            if (((IPaCoCheckInventory) this.pandoraCore$localSlot.container).pandoraCore$isInventory()) return;
            int index = this.pandoraCore$localSlot.index;
            if (index < 0 || index >= this.slots.size()) return;
            this.pandoraCore$targetIndexCache.add(index);
        }
    }
}