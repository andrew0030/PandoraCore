package com.github.andrew0030.pandora_core.client.render.instancing;

import com.github.andrew0030.pandora_core.utils.collection.CyclicStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL46;

public class InstanceFormat {
    public final CyclicStack<InstanceDataElement> elements;
    public final int stride;

    public InstanceFormat(InstanceDataElement... elements) {
        this.elements = new CyclicStack<>(elements[0], true);
        CyclicStack<InstanceDataElement> current = this.elements;
        int stride = elements[0].bytes();

        for (int i = 1; i < elements.length; i++) {
            CyclicStack<InstanceDataElement> build = new CyclicStack<>(elements[i], false);
            current.link(build);
            current = build;
            stride += elements[i].bytes();
        }
        current.link(this.elements);

        this.stride = stride;
    }

    public int offset(InstanceDataElement target) {
        int stride = 0;
        for (InstanceDataElement element : elements) {
            if (element == target) {
                return stride;
            }
            stride += element.bytes();
        }
        throw new RuntimeException("Element not found.");
    }

    public void setupState(VertexFormat format) {
        int attribute = format.getElements().size();
        int offset = 0;
        for (InstanceDataElement element : elements) {
            GlStateManager._enableVertexAttribArray(attribute);
            GlStateManager._vertexAttribPointer(attribute, element.size, element.type.glPrim, false, stride, offset);
            // TODO: apparently, vertexAttribDivisor is in a newer version of OpenGL than MC uses
            //       a fallback uniform based implementation will be necessary
            GL33.glVertexAttribDivisor(
                    attribute, 1
            );
            attribute++;
            offset += element.bytes();
        }
    }
}
