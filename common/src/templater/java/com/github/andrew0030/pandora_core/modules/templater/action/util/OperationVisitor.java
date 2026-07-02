package com.github.andrew0030.pandora_core.modules.templater.action.util;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.modules.templater.action.TransformVar;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.slf4j.Logger;
import tfc.glsl.value.MethodCallValue;
import tfc.glsl.value.OperationValue;
import tfc.glsl.visitor.GlslValueVisitorAdapter;

import java.util.Set;

public class OperationVisitor extends GlslValueVisitorAdapter {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "OperationVisitor");
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

    // TODO: This seems to currently print the error: "Unrecognized operation! %", is that an issue or...? ~andrew
    @Override
    public void visitOperation(OperationValue operationValue) {
        switch (operationValue.getOp()) {
            case "*" -> ops.add(TransformVar.Operation.MUL);
            case "/" -> ops.add(TransformVar.Operation.DIV);
            case "-" -> ops.add(TransformVar.Operation.SUB);
            case "+" -> ops.add(TransformVar.Operation.ADD);
            default -> LOGGER.error("Unrecognized operation! {}", operationValue.getOp());
        }
    }
}