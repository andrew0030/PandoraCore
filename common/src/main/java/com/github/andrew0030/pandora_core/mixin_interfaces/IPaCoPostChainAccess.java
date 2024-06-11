package com.github.andrew0030.pandora_core.mixin_interfaces;

import net.minecraft.client.renderer.PostPass;

import java.util.List;

public interface IPaCoPostChainAccess {
    List<PostPass> paCoGetPasses();
}
