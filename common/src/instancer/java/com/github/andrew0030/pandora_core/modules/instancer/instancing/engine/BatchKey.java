package com.github.andrew0030.pandora_core.modules.instancer.instancing.engine;

import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveDrawData;

public abstract class BatchKey {
    public abstract void flush(CollectiveDrawData data);
}
