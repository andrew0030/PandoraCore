package com.github.andrew0030.pandora_core.client.utils.shader.ln;

import java.util.ArrayList;
import java.util.List;

public class Line {
    public final int number;
    public final String text;

    public Line(int number, String text) {
        this.number = number;
        this.text = text;
    }

    public LineType type() {
        return LineType.LINE;
    }

    public enum LineType {
        LINE, COMMENT, DIRECTIVE;
    }

    // TODO: inVar class
    public List<String> resolveInputVar() {
        ArrayList<String> strs = new ArrayList<>();
        char[] chr = text.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chr.length; i++) {
            char c = chr[i];
            if (c == ';')
                continue;

            if (!Character.isWhitespace(c)) {
                builder.append(c);
            } else {
                String str = builder.toString().trim();
                if (!str.isEmpty()) {
                    strs.add(str);
                    builder = new StringBuilder();
                }
            }
        }

        String str = builder.toString().trim();
        if (!str.isEmpty()) {
            strs.add(str);
        }

        return strs;
    }
}
