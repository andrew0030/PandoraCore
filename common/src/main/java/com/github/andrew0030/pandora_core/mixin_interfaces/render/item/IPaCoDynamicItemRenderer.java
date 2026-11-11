package com.github.andrew0030.pandora_core.mixin_interfaces.render.item;

import com.github.andrew0030.pandora_core.client.render.dynamic.PaCoDynamicItemRenderer;

public interface IPaCoDynamicItemRenderer {
    void pandoraCore$setDynamicItemRenderer(PaCoDynamicItemRenderer renderer);
    PaCoDynamicItemRenderer pandoraCore$getDynamicItemRenderer();
}