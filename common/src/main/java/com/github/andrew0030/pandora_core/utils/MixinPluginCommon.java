package com.github.andrew0030.pandora_core.utils;

import com.github.andrew0030.pandora_core.PaCoTesting;
import com.github.andrew0030.pandora_core.utils.debug.PaCoProperties;

public class MixinPluginCommon extends MixinPluginBase {
    public MixinPluginCommon() {
        base = "com.github.andrew0030.pandora_core.mixin";
        addPkgLookup("compat");
//        addClassLookup("compat.gui.CatalogueModListScreenMixin");

        if (PaCoTesting.TEST_MODE) {
            addExclude("test.ShaderTemplateTest");
        }
		
	    if (!PaCoProperties.zinkPatch) {
			addExclude("test.ZinkPatch");
	    }
    }
}
