package com.github.andrew0030.pandora_core.client.render;

import org.lwjgl.opengl.*;

public class SupportChecker {
    /**
     * Whether the drivers/hardware supports {@link EXTCompiledVertexArray}
     * {@link EXTCompiledVertexArray} is meant for the case where you need to draw a VBO's mesh multiple times in a row without them changing position
     * For example: emissive overlays
     */
    public static final boolean SUPPORT_LOCKED_ARRAYS;
    // https://registry.khronos.org/OpenGL-Refpages/gl4/html/glMultiDrawElementsIndirect.xhtml
    public static final boolean SUPPORT_MULTIDRAW_INDIRECT;

    static {
        int num = GL11.glGetInteger(GL32.GL_NUM_EXTENSIONS);
        boolean lockArrays = false;
        boolean multidrawIndirect = false;
        for (int index = 0; index < num; index++) {
            String ext = GL32.glGetStringi(GL32.GL_EXTENSIONS, index);
            if (ext.equals("GL_EXT_compiled_vertex_array"))
                lockArrays = true;
            else if (ext.equals("GL_ARB_draw_indirect"))
                multidrawIndirect = true;
        }

        SUPPORT_LOCKED_ARRAYS = lockArrays;
        SUPPORT_MULTIDRAW_INDIRECT = multidrawIndirect;
    }
}
