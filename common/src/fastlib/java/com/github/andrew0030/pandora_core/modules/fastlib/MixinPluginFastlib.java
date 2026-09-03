package com.github.andrew0030.pandora_core.modules.fastlib;

import com.github.andrew0030.pandora_core.utils.MixinPluginBase;

public class MixinPluginFastlib extends MixinPluginBase {
    public MixinPluginFastlib() {
        base = "com.github.andrew0030.pandora_core.modules.templater.mixin";
	    
	    {
		    String dep = "net.irisshaders.iris.pipeline.programs.ExtendedShader";
		    addPackageDependency("render.iris.", dep);
	    }
	    {
		    String dep = "me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer";
		    addPackageDependency("render.sodium.", dep);
	    }
	    {
		    String dep = "net.optifine.shaders.Shaders";
		    addPackageDependency("render.optifine.", dep);
	    }
    }
}
