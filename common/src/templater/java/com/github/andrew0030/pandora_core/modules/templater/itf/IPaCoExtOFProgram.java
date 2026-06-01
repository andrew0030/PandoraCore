package com.github.andrew0030.pandora_core.modules.templater.itf;

import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.optifine.OFVtxAttribute;

public interface IPaCoExtOFProgram {
	boolean pandoraCore$usesAttrib(OFVtxAttribute attribute);
	
	/**
	 * Do not call, internal method
	 */
	@Deprecated
	void pandoraCore$enableAttrib(OFVtxAttribute attribute);
}
