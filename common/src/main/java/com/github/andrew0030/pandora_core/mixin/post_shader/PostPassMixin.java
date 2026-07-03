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
    @Unique private Collection<String> pandoraCore$tags = new ArrayList<>();

    @Override
    public void pandoraCore$addTag(String name) {
        pandoraCore$tags.add(name);
    }

    @Override
    public void pandoraCore$lockTags() {
        pandoraCore$tags = new ReadOnlySet<>(new HashSet<>(pandoraCore$tags));
    }

    @Override
    public Collection<String> pandoraCore$getTags() {
        return pandoraCore$tags;
    }

    @Override
    public boolean pandoraCore$hasTag(String tag) {
        return pandoraCore$tags.contains(tag);
    }
}