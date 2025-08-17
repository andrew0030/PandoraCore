package com.github.andrew0030.pandora_core.client.render.instancing.engine;

import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstanceRenderer;
import com.github.andrew0030.pandora_core.utils.CleanupUtils;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

import java.lang.ref.Cleaner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InstanceManager {
    Map<InstanceRenderer, BatchData> data = new HashMap<>();
    Map<InstanceRenderer, BatchData> thisFrame = new HashMap();

    static class DoClean implements Runnable {
        Map<InstanceRenderer, BatchData> data = new HashMap<>();
        Map<InstanceRenderer, BatchData> thisFrame = new HashMap();

        public DoClean(Map<InstanceRenderer, BatchData> data, Map<InstanceRenderer, BatchData> thisFrame) {
            this.data = data;
            this.thisFrame = thisFrame;
        }

        @Override
        public void run() {
            System.out.println("CLEANING!");
            Minecraft.getInstance().execute(() -> {
                for (BatchData value : data.values()) {
                    value.close();
                }
                // here more so for safety reasons
                Set<BatchData> datas = new HashSet<>(data.values());
                for (BatchData value : thisFrame.values()) {
                    if (!datas.contains(value))
                        value.close();
                }
            });
        }
    }

    Cleaner.Cleanable cleanable;

    public InstanceManager() {
        cleanable = CleanupUtils.registerCleanup(this, new DoClean(
                data, thisFrame
        ));
    }

    public void markFrame() {
        for (BatchData value : thisFrame.values()) {
            value.markFrame();
        }
        thisFrame.clear();
    }

    public BatchData getData(InstanceRenderer renderer) {
        return data.get(renderer);
    }

    public void markForFrame(InstanceRenderer renderer, BatchData data) {
        this.data.put(renderer, data);
        thisFrame.put(renderer, data);
    }

    public void drawFrame(Level level) {
        thisFrame.forEach((k, v) -> {
            k.flush(level, v);
        });
        VertexBuffer.unbind();
//        System.gc();
    }

    public void close() {
        cleanable.clean();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("Finalize called");
    }
}
