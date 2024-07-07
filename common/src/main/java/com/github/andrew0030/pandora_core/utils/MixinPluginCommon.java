package com.github.andrew0030.pandora_core.utils;

import com.github.andrew0030.pandora_core.PaCoTesting;

public class MixinPluginCommon extends MixinPluginBase {
    public MixinPluginCommon() {
        base = "com.github.andrew0030.pandora_core.mixin";
        addPkgLookup("compat");
//        addClassLookup("compat.gui.CatalogueModListScreenMixin");
        {
            String dep = "net.irisshaders.iris.pipeline.programs.ExtendedShader";
            addDependency("compat.iris.GlStateManagerMixin", dep);
            addDependency("compat.iris.ProgramMixin", dep);
            addDependency("compat.iris.ShaderInstance_CacheShadersMixin", dep);
            addDependency("compat.iris.ShaderInstanceMixin_CacheSrc_CondBindableMixin", dep);
        }

//        if (PaCoTesting.TEST_MODE) {
//            addExclude("test.ShaderTemplateTest");
//        }
    }
}
