package com.github.andrew0030.pandora_core.utils;

import com.github.andrew0030.pandora_core.PaCoTesting;

public class MixinPluginCommon extends MixinPluginBase {
    public MixinPluginCommon() {
        base = "com.github.andrew0030.pandora_core.mixin";
        addPkgLookup("compat");
//        addClassLookup("compat.gui.CatalogueModListScreenMixin");

        if (PaCoTesting.TEST_MODE) {
            addExclude("test.ShaderTemplateTest");
        }
		
	    String patchForZink = System.getProperty("paco.patches.zink_windows");
	    if (!(patchForZink != null && patchForZink.equals("true"))) {
			addExclude("test.ZinkPatch");
	    }
    }
}
