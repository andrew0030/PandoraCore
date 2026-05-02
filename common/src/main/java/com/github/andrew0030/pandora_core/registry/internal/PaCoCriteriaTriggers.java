package com.github.andrew0030.pandora_core.registry.internal;

import com.github.andrew0030.pandora_core.advancements.PaCoItemPlacedInContainerTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public class PaCoCriteriaTriggers {
    public static final PaCoItemPlacedInContainerTrigger ITEM_PLACED_IN_CONTAINER = CriteriaTriggers.register(new PaCoItemPlacedInContainerTrigger());

    public static void init() {}
}