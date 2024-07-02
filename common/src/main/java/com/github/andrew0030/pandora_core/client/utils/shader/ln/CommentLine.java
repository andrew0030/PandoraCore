package com.github.andrew0030.pandora_core.client.utils.shader.ln;

public class CommentLine extends Line {
    public CommentLine(int number, String text) {
        super(number, text);
    }

    @Override
    public LineType type() {
        return LineType.COMMENT;
    }
}
