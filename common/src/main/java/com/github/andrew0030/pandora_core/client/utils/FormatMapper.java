package com.github.andrew0030.pandora_core.client.utils;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.vertices.IrisVertexFormats;

public class FormatMapper {
    public static VertexFormat getEntity() {
        if (Iris.getIrisConfig().areShadersEnabled()) {
            return IrisVertexFormats.ENTITY;
        }
        return DefaultVertexFormat.NEW_ENTITY;
    }
}
