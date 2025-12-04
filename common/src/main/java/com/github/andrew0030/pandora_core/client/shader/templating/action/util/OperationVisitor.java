package com.github.andrew0030.pandora_core.client.shader.templating.action.util;

import com.github.andrew0030.pandora_core.client.shader.templating.action.TransformVar;
import tfc.glsl.value.MethodCallValue;
import tfc.glsl.value.OperationValue;
import tfc.glsl.visitor.GlslValueVisitorAdapter;

import java.util.Set;

public class OperationVisitor extends GlslValueVisitorAdapter {
    private final Set<TransformVar.Operation> ops;

    public OperationVisitor(Set<TransformVar.Operation> ops) {
        this.ops = ops;
    }

    @Override
    public void visitCall(MethodCallValue callValue) {
        switch (callValue.getName().asString()) {
            case "paco_rotateMatr" -> ops.add(TransformVar.Operation.ROTATE_MATRIX);
            case "paco_translateMatr" -> ops.add(TransformVar.Operation.TRANSLATE_MATRIX);
            case "paco_rotateQuat" -> ops.add(TransformVar.Operation.ROTATE_QUAT);
        }
    }

    @Override
    public void visitOperation(OperationValue operationValue) {
        switch (operationValue.getOp()) {
            case "*" -> ops.add(TransformVar.Operation.MUL);
            case "/" -> ops.add(TransformVar.Operation.DIV);
            case "-" -> ops.add(TransformVar.Operation.SUB);
            case "+" -> ops.add(TransformVar.Operation.ADD);
            default -> System.err.println("Unrecognized operation! " + operationValue.getOp());
        }
    }
}
