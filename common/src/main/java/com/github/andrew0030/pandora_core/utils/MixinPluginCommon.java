package com.github.andrew0030.pandora_core.utils;

public class MixinPluginCommon extends MixinPluginBase
{
    public MixinPluginCommon() {
        addPkgLookup("com.github.andrew0030.pandora_core.mixin.compat");
//        addClassLookup("com.github.andrew0030.pandora_core.mixin.compat.gui.CatalogueModListScreenMixin");
        addDependency("com.github.andrew0030.pandora_core.mixin.compat.iris.GlStateManagerMixin");
        addDependency("com.github.andrew0030.pandora_core.mixin.compat.iris.ProgramMixin");
        addDependency("com.github.andrew0030.pandora_core.mixin.compat.iris.ShaderInstance_CacheShadersMixin");
        addDependency("com.github.andrew0030.pandora_core.mixin.compat.iris.ShaderInstanceMixin_CacheSrc_CondBindableMixin");
    }
}
