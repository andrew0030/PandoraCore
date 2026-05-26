
package com.github.andrew0030.pandora_core.modules.templater.action.util;

import tfc.glsl.value.TokenValue;
import tfc.glsl.visitor.GlslValueVisitorAdapter;

public class RemappingVisitor extends GlslValueVisitorAdapter {
    private final String from, to;

    public RemappingVisitor(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void visitToken(TokenValue value) {
        if (value.getText().equals(from)) {
            value.setText(to);
        }
    }
}
