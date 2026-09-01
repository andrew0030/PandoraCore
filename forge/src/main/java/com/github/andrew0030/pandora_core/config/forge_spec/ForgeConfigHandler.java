package com.github.andrew0030.pandora_core.config.forge_spec;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.BooleanEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.CategoryEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.UnsupportedEntry;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolderCategory;
import com.github.andrew0030.pandora_core.config.manager.ForgeConfigDataHolderEntry;
import com.github.andrew0030.pandora_core.utils.function.TriConsumer;
import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nullable;
import java.util.*;

public class ForgeConfigHandler {
    private static final Map<Class<?>, TriConsumer<ForgeConfigSpec.ConfigValue<?>, ForgeConfigSpec.ValueSpec, String>> VALUE_HANDLERS = new HashMap<>();
    private final Map<String, ConfigDataHolder<?>> dataHolders = new LinkedHashMap<>();
    private final ForgeConfigSpec spec;

    public ForgeConfigHandler(ModConfig modConfig) {
        this.spec = (ForgeConfigSpec) modConfig.getSpec();
        // Initializes the: VALUE_HANDLERS
        this.initConfigCaches();
    }

    /**
     * This method initializes the annotation handler caches.
     * <br/>
     * <strong>Note</strong>: This method ensures type safety.
     */
    private void initConfigCaches() {
        VALUE_HANDLERS.put(Boolean.class, this::handleBoolean); // Boolean & boolean
        VALUE_HANDLERS.put(Integer.class, this::handleInteger); // Integer & int
//        VALUE_HANDLERS.put(Double.class, this::handleDouble);   // Double & double
//        VALUE_HANDLERS.put(Long.class, this::handleLong);       // Long & long
//        VALUE_HANDLERS.put(String.class, this::handleString);   // String
//        VALUE_HANDLERS.put(List.class, this::handleList);       // List
//        VALUE_HANDLERS.put(Enum.class, this::handleEnum);       // Enum

        // TODO maybe custom objects added using the raw "define" ?

//        VALUE_HANDLERS.put(PaCoConfigValues.CustomValue.class, this::handleCustomField);         // Custom Classes
//        VALUE_HANDLERS.put(PaCoConfigValues.CustomListValue.class, this::handleCustomListField); // Custom Classes List
//        VALUE_HANDLERS.put(PaCoConfigValues.GuiEntryKey.class, this::handleGuiEntryKey);         // GUI Entry Key
//        VALUE_HANDLERS.put(PaCoConfigValues.GuiEntryTooltip.class, this::handleGuiEntryTooltip); // GUI Entry Tooltip
//        VALUE_HANDLERS.put(PaCoConfigValues.Comment.class, this::handleComment);                 // Comments

        this.processForgeConfig(this.spec.getValues(), null);
    }

    // TODO update all the javadocs
    /** Handles loading all the fields and subclasses inside the config */
    private void processForgeConfig(UnmodifiableConfig config, @Nullable String category) {
        config.valueMap().forEach((key, value) -> {
            String path = StringUtil.isNullOrEmpty(category) ? key : category + "." + key;
            // Processes values
            if (value instanceof ForgeConfigSpec.ConfigValue<?> configValue) {
                TriConsumer<ForgeConfigSpec.ConfigValue<?>, ForgeConfigSpec.ValueSpec, String> consumer = VALUE_HANDLERS.get(configValue.getDefault().getClass());
                if (consumer != null)
                    consumer.accept(configValue, this.spec.getRaw(configValue.getPath()), path);
            }
            // Processes categories
            else if (value instanceof UnmodifiableConfig subConfig) {
                this.handleCategory(path);
                this.processForgeConfig(subConfig, path);
            }
        });
    }

    /** @return an ordered {@link ImmutableList}, containing a {@link ConfigDataHolder} for each value inside the {@link ModConfig} */
    public ImmutableList<ConfigDataHolder<?>> getConfigDataHolders() {
        return ImmutableList.copyOf(this.dataHolders.values());
    }

    // ######################################################################
    // #                           Value Handling                           #
    // ######################################################################

    private void handleBoolean(ForgeConfigSpec.ConfigValue<?> value, ForgeConfigSpec.ValueSpec spec, String path) {
        @SuppressWarnings("unchecked")
        ConfigDataHolder<Boolean> holder = (ConfigDataHolder<Boolean>) this.dataHolders.getOrDefault(path, new ForgeConfigDataHolderEntry<>(value, spec));
        holder.setPath(path);
        holder.setConfigEntryFactory(BooleanEntry::new); // TODO make this use the proper factory
        this.extractMetadata(holder, spec);
        this.dataHolders.put(path, holder);
    }

    private void handleInteger(ForgeConfigSpec.ConfigValue<?> value, ForgeConfigSpec.ValueSpec spec, String path) {
        @SuppressWarnings("unchecked")
        ForgeConfigDataHolderEntry<Integer> holder = (ForgeConfigDataHolderEntry<Integer>) this.dataHolders.getOrDefault(path, new ForgeConfigDataHolderEntry<>(value, spec));
        holder.setPath(path);
        holder.setConfigEntryFactory(UnsupportedEntry::new);  // TODO make this use the proper factory
        this.extractMetadata(holder, spec);
        this.dataHolders.put(path, holder);
    }

    public void handleCategory(String path) {
        ConfigDataHolderCategory categoryHolder = new ConfigDataHolderCategory();
        categoryHolder.setPath(path);
        categoryHolder.setConfigEntryFactory(CategoryEntry::new);

        List<String> pathList = Arrays.asList(path.split("\\."));
        // Extracts the category comment
        String comment = this.spec.getLevelComment(pathList);
        if (!StringUtil.isNullOrEmpty(comment)) categoryHolder.setComment(comment);
        // Extracts the category translation key
        String translationKey = this.spec.getLevelTranslationKey(pathList);
        if (!StringUtil.isNullOrEmpty(translationKey))
            categoryHolder.setKeyComponent(Component.translatable(translationKey));

        this.dataHolders.put(path, categoryHolder);
    }

    private <T extends Comparable<T>> void extractMetadata(ConfigDataHolder<T> holder, ForgeConfigSpec.ValueSpec spec) {
        // Extracts the translation key
        String translationKey = spec.getTranslationKey();
        if (!StringUtil.isNullOrEmpty(translationKey))
            holder.setKeyComponent(Component.translatable(translationKey));
        // Extract the comment
        String rawComment = spec.getComment();
        if (!StringUtil.isNullOrEmpty(rawComment))
            holder.setComment(this.cleanForgeComment(rawComment));
        // Extracts the ranges (Min/Max)
        if (holder instanceof ForgeConfigDataHolderEntry<T> entryHolder) {
            ForgeConfigSpec.Range<T> range = spec.getRange();
            if (range != null) this.extractRange(entryHolder, range);
        }
    }

    /** Strips forge's auto appended range/allowed values comment. */
    private String cleanForgeComment(String rawComment) {
        int rangeIdx = rawComment.indexOf("\nRange: ");
        int valIdx = rawComment.indexOf("\nAllowed Values: ");
        int cutoffIndex = -1;
        // If for some reason both exist (shouldn't happen) we remove up to whichever was found earlier
        if (rangeIdx >= 0 && valIdx >= 0) cutoffIndex = Math.min(rangeIdx, valIdx);
        // If only "Range: " was found
        else if (rangeIdx >= 0) cutoffIndex = rangeIdx;
        // If only "Allowed Values: " was found
        else if (valIdx >= 0) cutoffIndex = valIdx;
        // If any of the fluff was found its removed
        return cutoffIndex >= 0 ? rawComment.substring(0, cutoffIndex).trim() : rawComment.trim();
    }

    /** Extracts Min/Max values and applies them to the {@link ConfigDataHolder}. */
    private <T extends Comparable<T>> void extractRange(ForgeConfigDataHolderEntry<T> holder, ForgeConfigSpec.Range<T> range) {
        Object min = range.getMin();
        Object max = range.getMax();
        Number numMin = (min instanceof Number) ? (Number) min : null;
        Number numMax = (max instanceof Number) ? (Number) max : null;
        if (numMin != null || numMax != null) {
            holder.setRange(numMin, numMax);
            holder.setShowFullRange(true, numMin != null ? numMin : 0, numMax != null ? numMax : 0);
        }
    }
}