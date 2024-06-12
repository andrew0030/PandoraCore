package com.github.andrew0030.pandora_core.mixin;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoTagged;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlySet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Mixin(PostPass.class)
public class PostPassMixin implements IPaCoTagged {
    @Unique
    private Collection<String> paco_tags = new ArrayList<>();

    @Override
    public void addPaCoTag(String name) {
        paco_tags.add(name);
    }

    @Override
    public void lockPaCoTags() {
        paco_tags = new ReadOnlySet<>(new HashSet<>(paco_tags));
    }

    @Override
    public Collection<String> getPaCoTags() {
        return paco_tags;
    }

    @Override
    public boolean hasPaCoTag(String tag) {
        return paco_tags.contains(tag);
    }
}
