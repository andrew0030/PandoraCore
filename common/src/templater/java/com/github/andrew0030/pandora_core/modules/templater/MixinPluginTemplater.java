package com.github.andrew0030.pandora_core.modules.templater;

import com.github.andrew0030.pandora_core.utils.MixinPluginBase;

public class MixinPluginTemplater extends MixinPluginBase {
    public MixinPluginTemplater() {
        base = "com.github.andrew0030.pandora_core.modules.templater.mixin";
	    
	    {
		    String dep = "net.irisshaders.iris.pipeline.programs.ExtendedShader";
		    addPackageDependency("iris.", dep);
	    }
	    {
		    String dep = "net.optifine.shaders.Shaders";
		    addPackageDependency("optifine.", dep);
	    }
	    {
		    String dep = "qouteall.imm_ptl.core.api.PortalAPI";
		    addDependency("compat.IPCompat", dep);
	    }
    }
}
