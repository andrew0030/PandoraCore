package com.github.andrew0030.pandora_core.client.shader.holder;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoTagged;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.post.IPaCoPostChainAccess;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.post.IPaCoUniformAccess;
import com.github.andrew0030.pandora_core.utils.TagFilter;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.mojang.blaze3d.shaders.AbstractUniform;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.minecraft.client.renderer.PostPass;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PaCoUniformHolder implements Supplier<PaCoUniformHolder.UniformSetter>, IPaCoPostChainAccess {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "PaCoUniformHolder");

    final UniformSetter value = new UniformSetter();
    private IPaCoUniformAccess uniformAccess;
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
	        this.value.uniforms.clear();
	        this.value.uniforms = uniformAccess.pandoraCore$getUniforms(key);
	        this.value.readOnly = new ReadOnlyList<>(this.value.uniforms);
            isDirty = false;
        }

        return this.value;
    }

    @Override
    public List<PostPass> pandoraCore$getPasses() {
        return ((IPaCoPostChainAccess) uniformAccess).pandoraCore$getPasses();
    }
	
	@ApiStatus.Internal
	void link(IPaCoUniformAccess uniformAccess) {
		this.uniformAccess = uniformAccess;
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
                isDirty = false;
				
                passes.clear();
                for (PostPass pass : chainAccess.pandoraCore$getPasses()) {
                    if (tag.check((IPaCoTagged) pass)) {
                        passes.add(pass);
                    }
                }

                List<AbstractUniform> uniforms = new ArrayList<>();
                for (PostPass pass : pandoraCore$getPasses()) {
                    AbstractUniform uniform = pass.getEffect().getUniform(key);
                    if (uniform != null)
                        uniforms.add(uniform);
                }
				value.uniforms = uniforms;
				value.readOnly = new ReadOnlyList<>(uniforms);
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
	    ReadOnlyList<AbstractUniform> readOnly;

        public void set(float x) {
            for (AbstractUniform uniform : uniforms) {
                uniform.set(x);
            }
        }

        public void set(float x, float y) {
            for (AbstractUniform uniform : uniforms) {
                uniform.set(x, y);
            }
        }

        public void set(float x, float y, float z) {
            for (AbstractUniform uniform : uniforms) {
                uniform.set(x, y, z);
            }
        }

        public void set(float x, float y, float z, float w) {
            for (AbstractUniform uniform : uniforms) {
                uniform.set(x, y, z, w);
            }
        }

        public void set(int x) {
            for (AbstractUniform uniform : uniforms) {
                uniform.set(x);
            }
        }

        public void set(int x, int y) {
            for (AbstractUniform uniform : uniforms) {
                uniform.set(x, y);
            }
        }

        public void set(int x, int y, int z) {
            for (AbstractUniform uniform : uniforms) {
                uniform.set(x, y, z);
            }
        }

        public void set(int x, int y, int z, int w) {
            for (AbstractUniform uniform : uniforms) {
                uniform.set(x, y, z, w);
            }
        }
	    
	    public ReadOnlyList<AbstractUniform> getUniforms() {
		    return readOnly;
	    }
    }
}
