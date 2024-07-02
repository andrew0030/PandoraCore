package com.github.andrew0030.pandora_core.client.utils.shader.ln;

public class DirectiveLine extends Line {
    public DirectiveLine(int number, String text) {
        super(number, text);
    }

    @Override
    public LineType type() {
        return LineType.DIRECTIVE;
    }
}
