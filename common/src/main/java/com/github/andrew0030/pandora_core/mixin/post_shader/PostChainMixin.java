package com.github.andrew0030.pandora_core.mixin.post_shader;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoPostChainAccess;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoTagged;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoUniformAccess;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.shaders.AbstractUniform;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(PostChain.class)
public class PostChainMixin implements IPaCoUniformAccess, IPaCoPostChainAccess {
    @Shadow @Final private List<PostPass> passes;

    @Override
    public void pandoraCore$setUniform(String key, float value) {
        for (PostPass postPass : this.passes) {
            postPass.getEffect().safeGetUniform(key).set(value);
        }
    }

    @Override
    public List<AbstractUniform> pandoraCore$getUniforms(String key) {
        List<AbstractUniform> uniforms = new ArrayList<>();
        for (PostPass postPass : this.passes) {
            AbstractUniform u = postPass.getEffect().getUniform(key);
            if (u != null)
                uniforms.add(u);
        }
        return uniforms;
    }

    @Inject(at = @At("RETURN"), method = "parsePassNode")
    public void postParsePass(
            TextureManager textureManager, JsonElement json,
            CallbackInfo ci
    ) {
        JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "pass");
        PostPass pass = passes.get(passes.size() - 1);
        if (jsonObject.has("paco_tags")) {

            JsonArray array = GsonHelper.getAsJsonArray(jsonObject, "paco_tags");
            for (JsonElement jsonElement : array) {
                ((IPaCoTagged) pass).pandoraCore$addTag(
                        GsonHelper.convertToString(jsonElement, "paco_tag element")
                );
            }
        }
        ((IPaCoTagged) pass).pandoraCore$lockTags();
    }

    @Override
    public List<PostPass> pandoraCore$getPasses() {
        return passes;
    }
}