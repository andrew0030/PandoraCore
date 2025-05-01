package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.attachment;

public class AttachmentSpecifier {
    public final AttachmentType type;
    public final String source;
    public final String fileName;
    public final boolean preprocess;

    public AttachmentSpecifier(AttachmentType type, String source, String fileName) {
        this.type = type;
        this.source = source;
        this.fileName = fileName;
        this.preprocess = true;
    }

    public AttachmentSpecifier(AttachmentType type, String source, String fileName, boolean preprocess) {
        this.type = type;
        this.source = source;
        this.fileName = fileName;
        this.preprocess = preprocess;
    }
}
