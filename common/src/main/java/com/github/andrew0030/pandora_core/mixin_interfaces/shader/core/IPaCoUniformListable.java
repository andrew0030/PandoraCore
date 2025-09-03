package com.github.andrew0030.pandora_core.mixin_interfaces.shader.core;

import com.mojang.blaze3d.shaders.Uniform;

public interface IPaCoUniformListable {
    Iterable<Uniform> pandoraCore$listUniforms();
}