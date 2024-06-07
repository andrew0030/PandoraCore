package com.github.andrew0030.pandora_core.mixin;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoSetUniform;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PostChain.class)
public class PostChainMixin implements IPaCoSetUniform {
    @Shadow @Final private List<PostPass> passes;

    @Override
    public void setPaCoUniform(String key, float value) {
        for (PostPass postPass : this.passes) {
            postPass.getEffect().safeGetUniform(key).set(value);
        }
    }
}