package com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.attachment;

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
