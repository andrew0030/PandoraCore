package com.github.andrew0030.pandora_core.client.utils.shader;

import com.github.andrew0030.pandora_core.client.utils.shader.ln.Line;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;

import java.util.List;

public record ShaderFile(List<Line> lines) {
    public ShaderFile(List<Line> lines) {
        this.lines = new ReadOnlyList<>(lines);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Line line : lines) {
            builder.append(line.text);
        }
        return builder.toString();
    }
}
