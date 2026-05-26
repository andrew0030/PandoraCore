package com.github.andrew0030.pandora_core.modules.templater.action.util;

import com.github.andrew0030.pandora_core.modules.templater.TemplateTransformation;
import com.github.andrew0030.pandora_core.modules.templater.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.modules.templater.transformer.impl.TransformationContext;
import tfc.glsl.value.MethodCallValue;
import tfc.glsl.value.TokenValue;
import tfc.glsl.visitor.GlslValueVisitor;
import tfc.glsl.visitor.GlslValueVisitorAdapter;

public class Transformations {
    public static GlslValueVisitor callPatcher(TemplateTransformation transformation, TransformationContext context) {
        return new GlslValueVisitorAdapter() {
            @Override
            public void visitCall(MethodCallValue callValue) {
                String callName = transformation.getFunc(callValue.getName().asString());
                if (callName != null) {
                    callValue.setName(new TokenValue(callName));
                }
                callName = context.getFunc(callValue.getName().asString());
                if (callName != null) {
                    callValue.setName(new TokenValue(callName));
                }
                super.visitCall(callValue);
            }
        };
    }

    public static GlslValueVisitor valuePatcher(TemplateTransformation transformation, VariableMapper mapper, String fromName, String toName, TransformationContext context) {
        return new GlslValueVisitorAdapter() {
            @Override
            public void visitToken(TokenValue value) {
                String name = value.getText();
                String mapped = mapper.mapTo(null, name);
                if (fromName != null) {
                    if (name.equals(fromName)) mapped = toName;
                }
                if (mapped != null) {
                    value.setText(mapped);
                }
                super.visitToken(value);
            }
        };
    }
}
