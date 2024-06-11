package com.github.andrew0030.pandora_core.client.shader.holder;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoPostChainAccess;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoTagged;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoUniformAccess;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;
import com.mojang.blaze3d.shaders.AbstractUniform;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.minecraft.client.renderer.PostPass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PaCoUniformHolder implements Supplier<PaCoUniformHolder.UniformSetter>, IPaCoPostChainAccess {
    final UniformSetter value = new UniformSetter();
    private final IPaCoUniformAccess uniformAccess;
    private final String key;
    boolean isDirty = true;
    private final Map<String, TaggedUniformHolder> taggedHolders = new Object2ObjectRBTreeMap<>();

    public PaCoUniformHolder(IPaCoUniformAccess uniformAccess, String key) {
        this.uniformAccess = uniformAccess;
        this.key = key;
    }

    // TODO: not happy with this tbh
    //       will want to rework later on
    public TaggedUniformHolder tagged(String tag) {
        TaggedUniformHolder holder = taggedHolders.get(tag);
        if (holder == null) {
            taggedHolders.put(tag, holder = new TaggedUniformHolder(
                    this, key, tag
            ));
        }
        return holder;
    }

    @Override
    public UniformSetter get() {
        if (isDirty) {
            this.value.uniforms = uniformAccess.getPaCoUniforms(key);
            isDirty = false;
        }

        return this.value;
    }

    @Override
    public List<PostPass> paCoGetPasses() {
        return ((IPaCoPostChainAccess) uniformAccess).paCoGetPasses();
    }

    public class TaggedUniformHolder extends PaCoUniformHolder {
        private final String tag;
        private final List<PostPass> passes = new ArrayList<>();
        private final ReadOnlyList readOnlyView = new ReadOnlyList(passes);
        private final IPaCoPostChainAccess chainAccess;

        public TaggedUniformHolder(
                IPaCoPostChainAccess chainAccess,
                String key, String tag
        ) {
            super(null, key);
            this.chainAccess = chainAccess;
            this.tag = tag;
        }

        private void checkDirty() {
            if (isDirty) {
                passes.clear();
                for (PostPass pass : chainAccess.paCoGetPasses()) {
                    if (((IPaCoTagged) pass).hasPaCoTag(tag)) {
                        passes.add(pass);
                    }
                }

                isDirty = false;

                List<AbstractUniform> uniforms = new ArrayList<>();
                for (PostPass pass : paCoGetPasses()) {
                    AbstractUniform uform = pass.getEffect().getUniform(key);
                    if (uform != null)
                        uniforms.add(uform);
                }
                value.uniforms = uniforms;
            }
        }

        @Override
        public UniformSetter get() {
            checkDirty();
            return value;
        }

        @Override
        public List<PostPass> paCoGetPasses() {
            checkDirty();
            return readOnlyView;
        }
    }

    public class UniformSetter {
        List<AbstractUniform> uniforms;

        public void set(float value) {
            for (AbstractUniform uniform : uniforms) {
                uniform.set(value);
            }
        }
    }
}