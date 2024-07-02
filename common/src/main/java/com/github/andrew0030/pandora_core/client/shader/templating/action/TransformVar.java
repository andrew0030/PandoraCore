package com.github.andrew0030.pandora_core.client.shader.templating.action;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.AbstractTransformationProcessor;
import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;

public class TransformVar extends InsertionAction {
    String from;
    ArrayList<Pair<Operation, String>> ops = new ArrayList<>();

    public TransformVar(String from, String to) {
        this.from = from;
        for (String s : to.split(",")) {
            String[] splatter = s.trim().split(" ");
            ops.add(Pair.of(
                    Operation.forName(splatter[0].trim()),
                    splatter[1].trim()
            ));
        }
    }

    public boolean hasQuatRot() {
        for (Pair<Operation, String> op : ops) {
            if (op.getFirst() == Operation.ROTATE_QUAT)
                return true;
        }
        return false;
    }

    @Override
    public String afterInputVar(TemplateTransformation transformation, String type, String var) {
        if (!var.equals(from))
            return null;

        String leftHand = var;
        for (Pair<Operation, String> op : ops) {
            String rightHand = op.getSecond();
            Operation operation = op.getFirst();

            String format = operation.format;
            if (operation.func != null)
                format = format.replace("%func%", transformation.getFunc(operation.func));

            // TODO: automatic type conversions as required
            leftHand = format
                    .replace("%lh%", leftHand)
                    .replace("%rh%", rightHand);
        }

        return AbstractTransformationProcessor.TRANSFORM_INJECT
                .replace("%snowflake%", transformation.generateSnowflake())
                .replace("%type%", type)
                .replace("%variable%", var)
                .replace("%transform%", leftHand);
    }

    public enum Operation {
        ADD("add", "(%lh%+%rh%)"),
        SUB("subtract", "(%lh%-%rh%)"),
        MUL("multiply", "(%lh%*%rh%)"),
        DIV("divide", "(%lh%/%rh%)"),
        lhDIV("lhDivide", "(%rh%/%lh%)"),
        lhMUL("lhMultiply", "(%rh%+%lh%)"),
        lhSUB("lhSubtract", "(%rh%+%lh%)"),
        ROTATE_QUAT("rotateQuat", "%func%(%lh%,%rh%)", "rotateQuat"),
        ;

        final String name;
        final String format;
        final String func;

        protected static final Operation[] VALUES = values();

        Operation(String name, String format) {
            this.name = name;
            this.format = format;
            this.func = null;
        }

        Operation(String name, String format, String func) {
            this.name = name;
            this.format = format;
            this.func = func;
        }

        public static Operation forName(String s) {
            for (Operation value : VALUES) {
                if (value.name.equals(s)) {
                    return value;
                }
            }
            return null;
        }
    }
}
