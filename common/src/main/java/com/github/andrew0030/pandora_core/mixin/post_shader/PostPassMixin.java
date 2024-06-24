package com.github.andrew0030.pandora_core.mixin.post_shader;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoTagged;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlySet;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@Mixin(PostPass.class)
public class PostPassMixin implements IPaCoTagged {
    @Unique
    private Collection<String> paco_tags = new ArrayList<>();

    @Override
    public void pandoraCore$addTag(String name) {
        paco_tags.add(name);
    }

    @Override
    public void pandoraCore$lockTags() {
        paco_tags = new ReadOnlySet<>(new HashSet<>(paco_tags));
    }

    @Override
    public Collection<String> pandoraCore$getTags() {
        return paco_tags;
    }

    @Override
    public boolean pandoraCore$hasTag(String tag) {
        return paco_tags.contains(tag);
    }
}