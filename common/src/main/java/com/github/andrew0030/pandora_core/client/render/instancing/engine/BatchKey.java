package com.github.andrew0030.pandora_core.client.render.instancing.engine;

import com.github.andrew0030.pandora_core.client.render.collective.CollectiveDrawData;

public abstract class BatchKey {
    public abstract void flush(CollectiveDrawData data);
}
