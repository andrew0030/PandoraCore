package com.github.andrew0030.pandora_core.mixin.container.menu;

import com.github.andrew0030.pandora_core.mixin_interfaces.container.IPaCoExpandedMenuId;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin implements IPaCoExpandedMenuId {
    @Shadow @Final @Nullable private MenuType<?> menuType;
    @Unique private ResourceLocation pandoraCore$expandedMenuTypeId = null;

    @Nullable
    @Override
    public ResourceLocation pandoraCore$getExpandedMenuId() {
        if (this.pandoraCore$expandedMenuTypeId != null)
            return this.pandoraCore$expandedMenuTypeId;
        return null;
    }

    /** Adds {@link ResourceLocation} targets for menus that don't have a unique menuId. */
    @Override
    public void pandoraCore$setExpandedMenuId(ResourceLocation menuTypeId) {
        this.pandoraCore$expandedMenuTypeId = menuTypeId;
    }
}