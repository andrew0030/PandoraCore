package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.loader;

public class AttachmentSpecifier {
    AttachmentType type;
    String source;
    String fileName;
    boolean preprocess = true;

    public AttachmentSpecifier(AttachmentType type, String source, String fileName) {
        this.type = type;
        this.source = source;
        this.fileName = fileName;
    }

    public AttachmentSpecifier(AttachmentType type, String source, String fileName, boolean preprocess) {
        this.type = type;
        this.source = source;
        this.fileName = fileName;
        this.preprocess = preprocess;
    }
}
