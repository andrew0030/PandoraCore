package com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.blackhole;

import com.github.andrew0030.pandora_core.modules.templater.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.modules.templater.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.BaseProgram;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.BlackHoleProgram;
import com.mojang.blaze3d.shaders.AbstractUniform;

public class VoidShader extends TemplatedShader {
    BaseProgram program = BlackHoleProgram.INSTANCE;

    public static final VoidShader INSTANCE = new VoidShader(null, null, null);

    private VoidShader(TemplateLoader loader, TemplateShaderResourceLoader.TemplateStruct transformation, String template) {
        super(loader, transformation, template);
    }

    @Override
    public void apply() {
        program.bind();
    }

    @Override
    public void clear() {
        program.clear();
    }

    @Override
    public void upload() {
        program.upload();
    }

    @Override
    public void destroy() {
    }

    @Override
    public AbstractUniform getUniform(String name, int type, int count) {
        return program.getUniform(name, type, count);
    }

    @Override
    public int getAttributeLocation(String name) {
        return -1;
    }
	
	@Override
	public boolean isVanilla() {
		return true;
	}
}
