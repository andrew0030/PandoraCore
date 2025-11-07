package com.github.andrew0030.pandora_core.utils;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoTagged;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TagFilter implements Comparable<TagFilter> {
    Collection<String> tags;
    int hash;

    public TagFilter(String... tags) {
        this.tags = new ReadOnlyList<>(new ArrayList<>(new HashSet<>(
                Arrays.asList(tags)
        )));
        this.hash = Objects.hash(this.tags);
    }

    public static TagFilter of(String... tags) {
        return new TagFilter(tags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagFilter tagFilter = (TagFilter) o;
        if (tagFilter.hash != hash) return false;
        return Objects.equals(tags, tagFilter.tags);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public boolean check(IPaCoTagged object) {
        return object.pandoraCore$getTags().containsAll(tags);
    }

    @Override
    public int compareTo(@NotNull TagFilter o) {
        if (this.equals(o))
            return 0;

        int v = Integer.compare(hash, o.hash);
        if (v != 0) return v;

        v = Integer.compare(tags.size(), o.tags.size());
        if (v != 0) return v;

        Iterator<String> tagItr0 = tags.iterator();
        for (String tag : o.tags) {
            v = tagItr0.next().compareTo(tag);
            if (v != 0) return v;
        }

        return v;
    }
}
