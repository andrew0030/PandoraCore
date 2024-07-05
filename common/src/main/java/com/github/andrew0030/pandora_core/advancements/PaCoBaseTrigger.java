package com.github.andrew0030.pandora_core.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * An essentially empty CriterionTrigger class, that allows to easily create a lot
 * of Criterion, which can be triggered through code, using {@link PaCoBaseTrigger#trigger(ServerPlayer)},
 * as there aren't any conditions to trigger them from within a Json.
 */
public class PaCoBaseTrigger extends SimpleCriterionTrigger<PaCoBaseTrigger.TriggerInstance> {
    protected final ResourceLocation id;

    public PaCoBaseTrigger(@NotNull ResourceLocation id) {
        this.id = id;
    }

    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    @Override
    protected @NotNull TriggerInstance createInstance(@NotNull JsonObject jsonObject, @NotNull ContextAwarePredicate predicate, @NotNull DeserializationContext deserializationContext) {
        return new TriggerInstance(this.id, predicate);
    }

    /**
     * Called to trigger the Criteria through code.
     * @param serverPlayer The {@link ServerPlayer} that gets the advancement
     */
    public void trigger(ServerPlayer serverPlayer) {
        this.trigger(serverPlayer, triggerInstance -> true);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        public TriggerInstance(ResourceLocation id, ContextAwarePredicate player) {
            super(id, player);
        }
    }
}