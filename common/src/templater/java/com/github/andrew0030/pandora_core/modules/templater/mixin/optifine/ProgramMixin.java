package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine;

import com.github.andrew0030.pandora_core.modules.templater.itf.INamedShader;
import com.github.andrew0030.pandora_core.modules.templater.itf.IPaCoExtOFProgram;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.optifine.OFVtxAttribute;
import net.optifine.shaders.Program;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.EnumSet;

@Mixin(Program.class)
public abstract class ProgramMixin implements INamedShader, IPaCoExtOFProgram {
	@Shadow
	public abstract String getName();
	
	@Override
	public String pandoraCore$getName() {
		return getName();
	}
	
	EnumSet<OFVtxAttribute> attributes = EnumSet.noneOf(OFVtxAttribute.class);
	
	@Override
	public boolean pandoraCore$usesAttrib(OFVtxAttribute attribute) {
		return attributes.contains(attribute);
	}
	
	@Override
	public void pandoraCore$enableAttrib(OFVtxAttribute attribute) {
		attributes.add(attribute);
	}
}
