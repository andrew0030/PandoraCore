package com.github.andrew0030.pandora_core.client.utils.shader;

import com.github.andrew0030.pandora_core.client.utils.shader.ln.CommentLine;
import com.github.andrew0030.pandora_core.client.utils.shader.ln.DirectiveLine;
import com.github.andrew0030.pandora_core.client.utils.shader.ln.Line;

import java.util.ArrayList;

public class ShaderParser {
    public static ShaderFile parse(String text) {
        int lineNo = 0;
        boolean c0 = true;
        char[] chr = text.toCharArray();
        int len = chr.length;
        StringBuilder builder = new StringBuilder();

        ArrayList<Line> lines = new ArrayList<>();

        for (int i = 0; i < len; i++) {
            char c = chr[i];

            if (c == '\n') {
                lineNo += 1;
                c0 = true;
            }

            if (c == ';' || c == '{' || c == '}') {
                builder.append(c);
                lines.add(new Line(lineNo, builder.toString()));
                builder = new StringBuilder();
            } else if (c == '/' && chr[i + 1] == '/') {
                if (!builder.isEmpty())
                    lines.add(new Line(lineNo, builder.toString()));
                builder = new StringBuilder();
                while (c != '\n') {
                    i++;
                    builder.append(c);
                    c = chr[i];
                }
                builder.append("\n");
                lines.add(new CommentLine(lineNo, builder.toString()));
                builder = new StringBuilder();
                lineNo++;
            } else if (c == '/' && chr[i + 1] == '*') {
                if (!builder.isEmpty())
                    lines.add(new Line(lineNo, builder.toString()));
                builder = new StringBuilder();
                while (true) {
                    i++;
                    builder.append(c);
                    c = chr[i];
                    if (c == '*' && chr[i + 1] == '/') {
                        builder.append("*");
                        break;
                    }
                }
                lines.add(new CommentLine(lineNo, builder.toString()));
                builder = new StringBuilder();
                lineNo++;
            } else if (c == '#' && c0) {
                if (!builder.isEmpty())
                    lines.add(new Line(lineNo, builder.toString()));
                builder = new StringBuilder();
                while (c != '\n') {
                    i++;
                    builder.append(c);
                    c = chr[i];
                }
                builder.append("\n");
                lines.add(new DirectiveLine(lineNo, builder.toString()));
                builder = new StringBuilder();
                lineNo++;
            } else {
                builder.append(c);
            }

            if (!Character.isWhitespace(c))
                c0 = false;
        }

        return new ShaderFile(lines);
    }
}
