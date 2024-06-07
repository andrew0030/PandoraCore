package com.github.andrew0030.pandora_core.client.shader.holder;

import net.minecraft.client.renderer.PostChain;

import java.util.Map;

public interface IPaCoPostChainProcessor {
    void process(PostChain postChain, float partialTick, Map<String, Object> parameters);
}