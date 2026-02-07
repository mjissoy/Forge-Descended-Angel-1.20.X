package net.normlroyal.descendedangel.datagen.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.normlroyal.descendedangel.recipe.ModRecipeSerializers;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;


public class AltarRiteRecipeBuilder {

    public static final class RingEntry {
        private final Ingredient ingredient;
        private final boolean empty;

        private RingEntry(Ingredient ingredient, boolean empty) {
            this.ingredient = ingredient;
            this.empty = empty;
        }

        public static RingEntry of(Ingredient ingredient) {
            return new RingEntry(Objects.requireNonNull(ingredient), false);
        }

        public static RingEntry empty() {
            return new RingEntry(Ingredient.EMPTY, true);
        }

        private JsonObject toJson() {
            if (empty || ingredient == null || ingredient.isEmpty()) {
                JsonObject o = new JsonObject();
                o.addProperty("empty", true);
                return o;
            }
            var el = ingredient.toJson();
            if (el.isJsonObject()) return el.getAsJsonObject();

            JsonObject fallback = new JsonObject();
            fallback.add("ingredient", el);
            return fallback;
        }
    }

    private final Ingredient core;
    private final ItemStack result;

    private final List<RingEntry> ring = new ArrayList<>(8);

    private int requiredHaloTier = 0;
    private String displayType = "altar.descendedangel.rite";
    private @Nullable String resultNbtString = null;

    private AltarRiteRecipeBuilder(Ingredient core, ItemStack result) {
        this.core = core;
        this.result = result;

        for (int i = 0; i < 8; i++) ring.add(RingEntry.empty());
    }

    public static AltarRiteRecipeBuilder altar(Ingredient core, ItemStack result) {
        return new AltarRiteRecipeBuilder(core, result);
    }

    public AltarRiteRecipeBuilder displayType(String key) {
        if (key != null && !key.isBlank()) this.displayType = key;
        return this;
    }

    public AltarRiteRecipeBuilder requiredHaloTier(int tier) {
        this.requiredHaloTier = Math.max(0, tier);
        return this;
    }

    public AltarRiteRecipeBuilder ringSlot(int index, RingEntry entry) {
        if (index < 0 || index > 7) throw new IllegalArgumentException("ring slot must be 0..7");
        ring.set(index, entry == null ? RingEntry.empty() : entry);
        return this;
    }

    public AltarRiteRecipeBuilder ringSlot(int index, Ingredient ingredient) {
        return ringSlot(index, ingredient == null ? RingEntry.empty() : RingEntry.of(ingredient));
    }

    public AltarRiteRecipeBuilder ring(RingEntry... entries) {
        if (entries.length != 8) throw new IllegalArgumentException("ring(...) requires exactly 8 entries");
        for (int i = 0; i < 8; i++) ring.set(i, entries[i] == null ? RingEntry.empty() : entries[i]);
        return this;
    }

    public AltarRiteRecipeBuilder resultNbt(String nbtString) {
        this.resultNbtString = (nbtString == null || nbtString.isBlank()) ? null : nbtString;
        return this;
    }

    public void save(Consumer<FinishedRecipe> out, ResourceLocation id) {
        out.accept(new Result(id, core, ring, result, requiredHaloTier, displayType, resultNbtString));
    }

    private static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Ingredient core;
        private final List<RingEntry> ring;
        private final ItemStack result;
        private final int requiredHaloTier;
        private final String displayType;
        private final @Nullable String resultNbtString;

        private Result(ResourceLocation id,
                       Ingredient core,
                       List<RingEntry> ring,
                       ItemStack result,
                       int requiredHaloTier,
                       String displayType,
                       @Nullable String resultNbtString) {
            this.id = id;
            this.core = core;
            this.ring = ring;
            this.result = result;
            this.requiredHaloTier = requiredHaloTier;
            this.displayType = displayType;
            this.resultNbtString = resultNbtString;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("display_type", displayType);
            json.addProperty("required_halo_tier", requiredHaloTier);

            json.add("core", core.toJson());

            JsonArray ringArr = new JsonArray();
            for (int i = 0; i < 8; i++) ringArr.add(ring.get(i).toJson());
            json.add("ring", ringArr);

            JsonObject res = new JsonObject();
            res.addProperty("item", BuiltInRegistries.ITEM.getKey(result.getItem()).toString());
            if (result.getCount() != 1) res.addProperty("count", result.getCount());
            if (resultNbtString != null) res.addProperty("nbt", resultNbtString);
            json.add("result", res);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipeSerializers.ALTAR_RITE.get();
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
