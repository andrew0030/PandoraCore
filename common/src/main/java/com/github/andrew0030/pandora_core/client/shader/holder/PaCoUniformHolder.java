package com.github.andrew0030.pandora_core.client.shader.holder;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoPostChainAccess;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoTagged;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoUniformAccess;
import com.github.andrew0030.pandora_core.utils.TagFilter;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;
import com.mojang.blaze3d.shaders.AbstractUniform;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.minecraft.client.renderer.PostPass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PaCoUniformHolder implements Supplier<PaCoUniformHolder.UniformSetter>, IPaCoPostChainAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(PandoraCore.MOD_NAME + "::PaCoUniformHolder");

    final UniformSetter value = new UniformSetter();
    private final IPaCoUniformAccess uniformAccess;
    private final String key;
    boolean isDirty = true;
    private final Map<TagFilter, TaggedUniformHolder> taggedHolders = new Object2ObjectRBTreeMap<>();

    public PaCoUniformHolder(IPaCoUniformAccess uniformAccess, String key) {
        this.uniformAccess = uniformAccess;
        this.key = key;
    }

    public TaggedUniformHolder tagged(String tag) {
        return tagged(new TagFilter(tag));
    }

    public TaggedUniformHolder tagged(TagFilter filter) {
        TaggedUniformHolder holder = taggedHolders.get(filter);
        if (holder == null) {
            taggedHolders.put(filter, holder = new TaggedUniformHolder(
                    this, key, filter
            ));
        }
        return holder;
    }

    @Override
    public UniformSetter get() {
        if (isDirty) {
            this.value.uniforms = uniformAccess.pandoraCore$getUniforms(key);
            isDirty = false;
        }

        return this.value;
    }

    @Override
    public List<PostPass> pandoraCore$getPasses() {
        return ((IPaCoPostChainAccess) uniformAccess).pandoraCore$getPasses();
    }

    public class TaggedUniformHolder extends PaCoUniformHolder {
        private final TagFilter tag;
        private final List<PostPass> passes = new ArrayList<>();
        private final ReadOnlyList<PostPass> readOnlyView = new ReadOnlyList<>(passes);
        private final IPaCoPostChainAccess chainAccess;

        public TaggedUniformHolder(IPaCoPostChainAccess chainAccess, String key, TagFilter tag) {
            super(null, key);
            this.chainAccess = chainAccess;
            this.tag = tag;
        }

        private void checkDirty() {
            if (isDirty) {
                passes.clear();
                for (PostPass pass : chainAccess.pandoraCore$getPasses()) {
                    if (tag.check((IPaCoTagged) pass)) {
                        passes.add(pass);
                    }
                }

                isDirty = false;

                List<AbstractUniform> uniforms = new ArrayList<>();
                for (PostPass pass : pandoraCore$getPasses()) {
                    AbstractUniform uniform = pass.getEffect().getUniform(key);
                    if (uniform != null)
                        uniforms.add(uniform);
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
        public List<PostPass> pandoraCore$getPasses() {
            checkDirty();
            return readOnlyView;
        }

        @Override
        public TaggedUniformHolder tagged(TagFilter filter) {
            LOGGER.warn("Calling tagged on a TaggedUniformHolder is not advised; doing so creates extra objects in memory which are less likely to be shared between mods.");
            return super.tagged(filter);
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