package com.github.andrew0030.pandora_core.mixin.compat.instancing;

import com.github.andrew0030.pandora_core.client.render.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.client.render.instancing.engine.PacoInstancingLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public class EngineAttacher implements PacoInstancingLevel {
    InstanceManager manager = new InstanceManager();

    @Override
    public InstanceManager getManager() {
        return manager;
    }
}
