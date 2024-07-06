package com.github.andrew0030.pandora_core.utils.enums;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

public enum NumericPrimitive {
    BYTE(GL11.GL_BYTE, 1),
    SHORT(GL11.GL_SHORT, 2),
    INT(GL11.GL_INT, 4),
    LONG(-1, 8), // it appears longs don't have a gl primitive
    FLOAT(GL11.GL_FLOAT, 4),
    DOUBLE(GL11.GL_DOUBLE, 8),
    ;

    public final int glPrim;
    public final int size;

    NumericPrimitive(int glPrim, int size) {
        this.glPrim = glPrim;
        this.size = size;
    }
}
