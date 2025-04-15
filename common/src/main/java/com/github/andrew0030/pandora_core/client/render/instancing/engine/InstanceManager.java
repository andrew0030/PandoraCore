package com.github.andrew0030.pandora_core.client.render.instancing.engine;

import com.github.andrew0030.pandora_core.client.render.collective.CollectiveDrawData;
import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstanceRenderer;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class InstanceManager {
    Map<InstanceRenderer, CollectiveDrawData> data = new HashMap<>();
    Map<InstanceRenderer, CollectiveDrawData> thisFrame = new HashMap();

    public InstanceManager() {
    }

    public void markFrame() {
        for (CollectiveDrawData value : thisFrame.values()) {
            value.deactivate();
        }
        thisFrame.clear();
    }

    public CollectiveDrawData getData(InstanceRenderer renderer) {
        return data.get(renderer);
    }

    public void markForFrame(InstanceRenderer renderer, CollectiveDrawData data) {
        this.data.put(renderer, data);
        thisFrame.put(renderer, data);
    }

    public void drawFrame(Level level) {
        thisFrame.forEach((k, v) -> {
            k.flush(level, v);
        });
        VertexBuffer.unbind();
    }
}
