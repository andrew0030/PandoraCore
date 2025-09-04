package com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris;

import net.irisshaders.iris.gl.program.ProgramImages;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;

public interface IPacoAccessInitializables {
    ProgramSamplers pandoraCore$getSamplers();
    ProgramImages pandoraCore$getImages();
    ProgramUniforms pandoraCore$getUniforms();
}
