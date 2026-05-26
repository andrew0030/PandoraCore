package com.github.andrew0030.pandora_core.modules.instancer.state;

import com.github.andrew0030.pandora_core.modules.templater.wrapper.ShaderWrapper;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.blackhole.VoidShader;
import com.github.andrew0030.pandora_core.utils.shader_checker.ShaderChecker;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.function.Supplier;

public class PaCoShaderStateShard extends RenderStateShard.ShaderStateShard {
    Runnable setup;
    Runnable clear;
	Supplier<Boolean> isVoid;
	Supplier<Boolean> canDraw;

    // only useful for immediate use render types
    public PaCoShaderStateShard(TemplatedShader shader) {
        super();
        setup = () -> {
            shader.apply();
            shader.upload();
        };
        clear = shader::clear;
		
		isVoid = () -> shader == VoidShader.INSTANCE;
	    canDraw = () -> {
		    if (isVoid.get()) return false;
		    
		    // TODO: render state aware
		    if (ShaderChecker.isShaderActive()) {
			    return !shader.isVanilla();
		    }
		    
		    return true;
	    };
    }

    public PaCoShaderStateShard(ShaderWrapper shaderWrapper) {
        super();
        setup = () -> {
            shaderWrapper.apply();
            shaderWrapper.upload();
        };
        clear = shaderWrapper::clear;
	    isVoid = () -> shaderWrapper.getActiveUnwrap() == VoidShader.INSTANCE;
	    canDraw = () -> {
		    if (isVoid.get()) return false;
		    
			// TODO: is this sufficient shader state tracking?
		    if (PaCoRenderState.isGUI()) {
			    return shaderWrapper.getActiveUnwrap().isVanilla();
		    } else if (ShaderChecker.isShaderActive()) {
			    return !shaderWrapper.getActiveUnwrap().isVanilla();
		    }
		    
		    return true;
	    };
    }

    // might as well
    public PaCoShaderStateShard(ShaderInstance shaderInstance) {
        super();
        setup = () -> shaderInstance.apply();
        clear = shaderInstance::clear;
    }

    @Override
    public void setupRenderState() {
        setup.run();
    }

    @Override
    public void clearRenderState() {
        clear.run();
    }
	
	public boolean isVoid() {
		return isVoid.get();
	}
	
	public boolean shouldRender() {
		return canDraw.get();
	}
}
