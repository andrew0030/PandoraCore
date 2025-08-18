package com.github.andrew0030.pandora_core.client.render.instancing.engine;

import com.github.andrew0030.pandora_core.client.render.collective.CollectiveDrawData;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BatchData {
    Map<BatchKey, CollectiveDrawData> datas = new HashMap<>();
    Supplier<CollectiveDrawData> datagen;
    Deque<CollectiveDrawData> inactiveData = new ArrayDeque<>();

    public BatchData(Supplier<CollectiveDrawData> datagen) {
        this.datagen = datagen;
    }

    public void close() {
        for (CollectiveDrawData value : datas.values()) {
            value.close();
        }
    }

    protected CollectiveDrawData nextData() {
        if (!inactiveData.isEmpty()) {
            return inactiveData.removeFirst();
        }
        return datagen.get();
    }

    public CollectiveDrawData buildBatch(BatchKey key) {
        CollectiveDrawData data = datas.get(key);
        if (data == null) {
            data = nextData();
            datas.put(key, data);
        }
        return data;
    }

    public void flush() {
        datas.forEach(BatchKey::flush);
    }

    public void markFrame() {
        for (CollectiveDrawData value : datas.values()) {
            value.deactivate();
            value.wipeIndices();
            inactiveData.add(value);
        }
        datas.clear();
    }
}
