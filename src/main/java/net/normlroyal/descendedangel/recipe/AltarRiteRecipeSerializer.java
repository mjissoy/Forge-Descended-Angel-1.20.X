package net.normlroyal.descendedangel.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class AltarRiteRecipeSerializer implements RecipeSerializer<AltarRiteRecipe> {

    @Override
    public AltarRiteRecipe fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("core"))
            throw new JsonParseException("Missing required field 'core' for altar_rite recipe " + id);
        Ingredient core = Ingredient.fromJson(json.get("core"));
        List<Ingredient> ring = new ArrayList<>(8);
        JsonArray ringArr = GsonHelper.getAsJsonArray(json, "ring", new JsonArray());

        if (ringArr.size() > 8)
            throw new JsonParseException("Altar ring can have at most 8 ingredients (got " + ringArr.size() + ") in " + id);
        for (int i = 0; i < 8; i++) {
            if (i >= ringArr.size()) {
                ring.add(Ingredient.EMPTY);
                continue;
            }
            var el = ringArr.get(i);
            if (el.isJsonObject()) {
                JsonObject o = el.getAsJsonObject();
                if (GsonHelper.getAsBoolean(o, "empty", false) || o.size() == 0) {
                    ring.add(Ingredient.EMPTY);
                } else {
                    ring.add(Ingredient.fromJson(o));
                }
            } else {
                ring.add(Ingredient.fromJson(el));
            }
        }

        if (!json.has("result"))
            throw new JsonParseException("Missing required field 'result' for altar_rite recipe " + id);
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

        int haloTier = GsonHelper.getAsInt(json, "required_halo_tier", 0);
        String displayKey = GsonHelper.getAsString(json, "display_type", "altar.descendedangel.rite");
        return new AltarRiteRecipe(id, core, ring, result, haloTier, displayKey);
    }

    @Override
    public AltarRiteRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient core = Ingredient.fromNetwork(buf);

        List<Ingredient> ring = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ring.add(Ingredient.fromNetwork(buf));
        }

        ItemStack result = buf.readItem();
        int haloTier = buf.readInt();
        String displayKey = buf.readUtf();

        return new AltarRiteRecipe(id, core, ring, result, haloTier, displayKey);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, AltarRiteRecipe recipe) {
        recipe.getCore().toNetwork(buf);
        for (Ingredient ing : recipe.getRing()) ing.toNetwork(buf);
        buf.writeItem(recipe.getResult().copy());
        buf.writeInt(recipe.requiredHaloTier());
        buf.writeUtf(recipe.displayTypeKey());
    }
}
