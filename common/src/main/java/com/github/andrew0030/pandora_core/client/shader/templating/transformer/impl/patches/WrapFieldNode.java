package com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl.patches;

import io.github.ocelot.glslprocessor.api.grammar.GlslSpecifiedType;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;
import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewFieldNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class WrapFieldNode implements GlslNode {
    GlslNode node;
    String text;

    public WrapFieldNode(GlslNode node, String text) {
        this.node = node;
        this.text = text;
    }

    @Override
    public String toSourceString() {
        return node.toSourceString() + "\n" + text + "\n";
    }

    @Override
    public @Nullable GlslSpecifiedType getType() {
        return node.getType();
    }

    @Override
    public List<GlslNode> toList() {
        return node.toList();
    }

    @Override
    public @Nullable GlslNodeList getBody() {
        return node.getBody();
    }

    @Override
    public boolean setBody(Collection<GlslNode> body) {
        return node.setBody(body);
    }

    @Override
    public boolean setBody(GlslNode... body) {
        return node.setBody(body);
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        node.visit(visitor);
    }

    @Override
    public GlslNodeType getNodeType() {
        return node.getNodeType();
    }

    @Override
    public Stream<GlslNode> stream() {
        return node.stream();
    }
}
