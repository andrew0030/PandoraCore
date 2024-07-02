package com.github.andrew0030.pandora_core.utils;

public class MixinPluginCommon extends MixinPluginBase
{
    public MixinPluginCommon() {
        addPkgLookup("com.github.andrew0030.pandora_core.mixin.compat");
//        addClassLookup("com.github.andrew0030.pandora_core.mixin.compat.gui.CatalogueModListScreenMixin");
    }
}
