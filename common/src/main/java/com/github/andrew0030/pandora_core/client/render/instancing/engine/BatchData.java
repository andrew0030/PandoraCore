package com.github.andrew0030.pandora_core.client.render.instancing.engine;

import com.github.andrew0030.pandora_core.client.render.collective.CollectiveDrawData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BatchData {
    Map<BatchKey, CollectiveDrawData> datas = new HashMap<>();
    Supplier<CollectiveDrawData> datagen;

    public BatchData(Supplier<CollectiveDrawData> datagen) {
        this.datagen = datagen;
    }
}
