package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program;

public enum AttachmentType {
    FRAGMENT("fsh"), VERTEX("vsh"),
    GEOMETRY("gsh"), TESSELATION_CONTROL("tsc"), TESSELATION_EVAL("tes");

    final String strName;

    AttachmentType(String strName) {
        this.strName = strName;
    }

    public String strName() {
        return strName;
    }
}
