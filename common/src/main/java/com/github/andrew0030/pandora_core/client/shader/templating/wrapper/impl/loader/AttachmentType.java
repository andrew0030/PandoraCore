package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.loader;

public enum AttachmentType {
    FRAGMENT("fsh"), VERTEX("vsh"),
    GEOMETRY("gsh"), TESSELATION_CONTROL("tese"), TESSELATION_EVAL("tesc");

    final String strName;

    AttachmentType(String strName) {
        this.strName = strName;
    }

    public String strName() {
        return strName;
    }
}
