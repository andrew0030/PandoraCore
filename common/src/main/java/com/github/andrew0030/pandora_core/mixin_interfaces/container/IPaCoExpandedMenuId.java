package com.github.andrew0030.pandora_core.mixin_interfaces.container;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public interface IPaCoExpandedMenuId {
    default @Nullable ResourceLocation pandoraCore$getExpandedMenuId() { return null; }
    default void pandoraCore$setExpandedMenuId(ResourceLocation menuTypeId) {}
}