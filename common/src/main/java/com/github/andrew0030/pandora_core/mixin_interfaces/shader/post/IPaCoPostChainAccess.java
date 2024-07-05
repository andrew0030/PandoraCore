package com.github.andrew0030.pandora_core.mixin_interfaces.shader.post;

import net.minecraft.client.renderer.PostPass;

import java.util.List;

public interface IPaCoPostChainAccess {
    List<PostPass> pandoraCore$getPasses();
}