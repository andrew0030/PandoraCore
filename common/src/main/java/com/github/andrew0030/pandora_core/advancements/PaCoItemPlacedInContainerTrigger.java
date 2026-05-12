package com.github.andrew0030.pandora_core.advancements;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <h2>Trigger: {@code pandora_core:item_placed_in_container}</h2>
 *
 * <p>Fires when a player places one or more items (same stack) into a container via
 * any interaction (shift-click, drag-distribution, normal placement, or hotkey swap).</p>
 *
 * <h3>JSON Structure</h3>
 * <pre>{@code
 * {
 *   "trigger": "pandora_core:item_placed_in_container",
 *   "conditions": {
 *     "menu": "namespace:menu_id",          // REQUIRED: Exact menu ResourceLocation to match
 *     "items": [                            // OPTIONAL: Array of item predicates (OR logic, see bellow)
 *       {
 *         "items": ["minecraft:diamond"],   // Item ID that will be matched
 *         "count": { "min": 10 },           // Stack size in a SINGLE slot (see note below)
 *         "durability": { "max": 50 },      // Remaining durability range
 *         "enchantments": [...],            // Enchantment conditions
 *         "nbt": "{...}",                   // Raw NBT string (escape quotes with \")
 *         "tag": "namespace:tag_id"         // ItemTag to match
 *         // ... all fields within one {} use AND logic
 *       }
 *     ]
 *   }
 * }
 * }</pre>
 *
 * <h3>Fields:</h3>
 * <ul>
 *   <li>
 *     <strong>{@code menu} is REQUIRED:</strong> Advancements without this field will fail to parse!<br/>
 *     Specify the exact {@link MenuType} registry ID (e.g. {@code "minecraft:generic_9x3"}, {@code "minecraft:hopper"},
 *     {@code "example_mod:example_menu"}). See {@link MenuType} for all Minecraft menus.
 *   </li>
 *   <li>
 *     <strong>{@code items} is OPTIONAL:</strong> If omitted, <em>any</em> non empty item inserted
 *     into the specified menu will trigger the advancement.
 *   </li>
 * </ul>
 *
 * <h3>Trigger Logic:</h3>
 * <ul>
 *   <li>
 *     <strong>OR logic between predicates:</strong> If multiple objects are provided in the
 *     {@code items} array, the advancement triggers if <em>any single predicate</em> matches
 *     <em>the inserted stack/s</em>.<br/>
 *     Example:
 *     <pre>{@code
 *       "items": [
 *         { "items": ["minecraft:diamond"], "count": { "min": 10 } },
 *         { "items": ["minecraft:gold_ingot"] }
 *       ]
 *       }
 *     </pre>
 *     Triggers if there is at least 10 diamonds in the placed slot <strong>OR</strong> gold ingots are placed.
 *   </li>
 *   <br/>
 *   <li>
 *     <strong>AND logic within a predicate:</strong> All fields inside a single {@code {}} object
 *     must match the same item stack. Example:
 *     <pre>{@code
 *       {
 *         "items": ["minecraft:diamond_sword"],
 *         "enchantments": [{ "enchantment": "minecraft:sharpness", "levels": { "min": 5 } }],
 *         "count": { "min": 1 }
 *       }
 *       }
 *     </pre>
 *     Triggers only if a diamond sword with Sharpness {@code V+} is placed.
 *   </li>
 *   <br/>
 *   <li>
 *     <strong>{@code count} checks per-slot, not aggregate:</strong> The {@code count} predicate
 *     validates the stack size in a <em>single slot</em>, not the total inserted across all slots.
 *     Example: Inserting 16 diamonds that split into two slots of 8 each will <em>not</em> match
 *     {@code "count": { "min": 10 }}, because no individual slot contains ≥10 diamonds.<br/>
 *     This matches vanilla's {@link ItemPredicate} behavior.
 *   </li>
 * </ul>
 *
 * <h3>Why OR Logic?</h3>
 * <p>Unlike {@code minecraft:inventory_changed} (which uses AND logic for its {@code items} array),
 * this trigger uses OR logic because:</p>
 * <ul>
 *   <li>
 *     <strong>Single item type per interaction:</strong> Vanilla's {@code moveItemStackTo},
 *     and other insertion logic only move stacks of <em>one item type</em> per {@code doClick}
 *     call. You cannot insert diamonds AND gold ingots in the same click action.
 *   </li>
 *   <li>
 *     <strong>AND logic would never match:</strong> Requiring multiple item types via AND logic
 *     would make advancements impossible to complete, as the game physically prevents inserting
 *     multiple different stacks in one interaction.
 *   </li>
 *   <li>
 *     <strong>OR matches real usage:</strong> Datapack authors typically want "trigger if the player
 *     places <em>any of these valid items</em>", which OR logic provides naturally.
 *   </li>
 * </ul>
 *
 * <p><strong>Need AND logic across multiple item types?</strong> Use multiple criteria with the
 * {@code requirements} field:
 * <pre>{@code
 * "criteria": {
 *   "place_diamond": { "trigger": "...", "conditions": { "items": [{ "items": ["minecraft:diamond"] }] } },
 *   "place_gold": { "trigger": "...", "conditions": { "items": [{ "items": ["minecraft:gold_ingot"] }] } }
 * },
 * "requirements": [["place_diamond"], ["place_gold"]]
 * }
 * </pre>
 * Requires both criteria to trigger (i.e., two separate placement actions).</p>
 */
public class PaCoItemPlacedInContainerTrigger extends SimpleCriterionTrigger<PaCoItemPlacedInContainerTrigger.TriggerInstance> {
    private static final ResourceLocation ID = new ResourceLocation(PandoraCore.MOD_ID, "item_placed_in_container");

    public PaCoItemPlacedInContainerTrigger() {}

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    @Override
    protected @NotNull PaCoItemPlacedInContainerTrigger.TriggerInstance createInstance(@NotNull JsonObject jsonObject, @NotNull ContextAwarePredicate predicate, @NotNull DeserializationContext deserializationContext) {
        ResourceLocation menu = new ResourceLocation(GsonHelper.getAsString(jsonObject, "menu"));
        ItemPredicate[] items = ItemPredicate.fromJsonArray(jsonObject.get("items"));
        return new TriggerInstance(predicate, menu, items);
    }

    public void trigger(ServerPlayer player, MenuType<?> menuType, ResourceLocation expandedMenuId, List<ItemStack> insertedItems) {
        ResourceLocation menuId = BuiltInRegistries.MENU.getKey(menuType);
        this.trigger(player, menuId, expandedMenuId, insertedItems);
    }

    public void trigger(ServerPlayer player, ResourceLocation menuId, ResourceLocation expandedMenuId, List<ItemStack> insertedItems) {
        this.trigger(player, instance -> instance.matches(menuId, expandedMenuId, insertedItems));
    }

    /** Holds the parsed conditions for a single criterion using this trigger. */
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ResourceLocation menuId;
        private final ItemPredicate[] predicates;

        public TriggerInstance(ContextAwarePredicate predicate, ResourceLocation menuId, ItemPredicate[] predicates) {
            super(ID, predicate);
            this.menuId = menuId;
            this.predicates = predicates;
        }

        @Override
        public @NotNull JsonObject serializeToJson(@NotNull SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            json.addProperty("menu", this.menuId.toString());
            if (this.predicates.length > 0) {
                JsonArray array = new JsonArray();
                for (ItemPredicate predicate : this.predicates)
                    array.add(predicate.serializeToJson());
                json.add("items", array);
            }
            return json;
        }

        public boolean matches(ResourceLocation targetMenuId, ResourceLocation targetAltMenuId, List<ItemStack> insertedItems) {
            // If no ID was specified we can't target a menu
            if (this.menuId == null) return false;
            // If we aren't in the desired menu it is not a match
            if (!this.menuId.equals(targetMenuId) && !this.menuId.equals(targetAltMenuId)) return false;
            // Checks if there are any predicates
            boolean isPredicatesEmpty = this.predicates.length == 0;

            for (ItemStack stack : insertedItems) {
                // An item needs to be inserted for an insertion to count as valid
                if (stack.isEmpty()) continue;
                // If there were no item predicates specified, any insertion is a match
                if (isPredicatesEmpty) return true;
                // If the inserted stack isn't empty, and there are specified predicates, we check for a match
                for (ItemPredicate predicate : this.predicates) {
                    if (predicate.matches(stack)) {
                        return true;
                    }
                }
            }
            // If no matches could be found the stack is not valid
            return false;
        }
    }
}