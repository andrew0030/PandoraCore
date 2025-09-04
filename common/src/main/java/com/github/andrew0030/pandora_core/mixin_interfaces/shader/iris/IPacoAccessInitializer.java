package com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris;

import net.irisshaders.iris.gl.program.GlUniform1iCall;

import java.util.List;

public interface IPacoAccessInitializer {
    List<GlUniform1iCall> pandoraCore$getInitializer();
    void pandoraCore$setInitializer(List<GlUniform1iCall> initializer);
}
