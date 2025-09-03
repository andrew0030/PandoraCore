package com.github.andrew0030.pandora_core.mixin.shader;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoUniformListable;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ShaderInstance.class)
public class ShaderInstanceMixin implements IPaCoUniformListable {
    @Shadow
    @Final
    private List<Uniform> uniforms;

    @Override
    public Iterable<Uniform> pandoraCore$listUniforms() {
        return uniforms;
    }
}
