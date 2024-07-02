package com.github.andrew0030.pandora_core.utils;

public class MixinPluginFabric extends MixinPluginBase
{
    public MixinPluginFabric() {
        addPkgLookup("com.github.andrew0030.pandora_core.mixin.compat");
//        addClassLookup("com.github.andrew0030.pandora_core.mixin.compat.gui.CatalogueModListScreenMixin");
//        addClassLookup("com.github.andrew0030.pandora_core.mixin.compat.gui.ModsScreenMixin");
    }
}