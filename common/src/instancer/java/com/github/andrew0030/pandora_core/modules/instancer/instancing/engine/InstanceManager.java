package com.github.andrew0030.pandora_core.modules.instancer.instancing.engine;

import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstanceRenderer;
import com.github.andrew0030.pandora_core.utils.CleanupUtils;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

import java.lang.ref.Cleaner;
import java.util.*;

public class InstanceManager {
    Map<InstanceRenderer, BatchData> data = new HashMap<>();
    Map<InstanceRenderer, BatchData> thisFrame = new HashMap();
	Set<InstanceRenderer> nullInvoke = new HashSet<>();

    static class DoClean implements Runnable {
        Map<InstanceRenderer, BatchData> data = new HashMap<>();
        Map<InstanceRenderer, BatchData> thisFrame = new HashMap();

        public DoClean(Map<InstanceRenderer, BatchData> data, Map<InstanceRenderer, BatchData> thisFrame) {
            this.data = data;
            this.thisFrame = thisFrame;
        }

        @Override
        public void run() {
//            System.out.println("CLEANING!");
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
            if (value != null)
				value.markFrame();
        }
        thisFrame.clear();
		nullInvoke.clear();
    }

    public BatchData getData(InstanceRenderer renderer) {
        return data.get(renderer);
    }

    public void markForFrame(InstanceRenderer renderer, BatchData data) {
		if (data != null) {
			Object o = thisFrame.put(renderer, data);
			if (o == null)
				this.data.put(renderer, data);
		} else {
			nullInvoke.add(renderer);
		}
    }

    public void drawFrame(InstancingEnvironment env) {
        thisFrame.forEach((k, v) -> {
            k.flush(env, v);
        });
	    for (InstanceRenderer instanceRenderer : nullInvoke)
		    instanceRenderer.flush(env, null);
        VertexBuffer.unbind();
    }

    public void close() {
        cleanable.clean();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
//        System.out.println("Finalize called");
    }
}
