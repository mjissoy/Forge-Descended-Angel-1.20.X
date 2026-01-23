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
        Ingredient core = Ingredient.fromJson(json.get("core"));

        JsonArray ringArr = json.getAsJsonArray("ring");
        if (ringArr.size() > 8)
            throw new JsonParseException("Altar ring can have at most 8 ingredients");

        List<Ingredient> ring = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            if (i >= ringArr.size()) {
                ring.add(Ingredient.EMPTY);
                continue;
            }
            if (ringArr.get(i).isJsonObject()) {
                JsonObject o = ringArr.get(i).getAsJsonObject();
                if (GsonHelper.getAsBoolean(o, "empty", false)) {
                    ring.add(Ingredient.EMPTY);
                } else {
                    ring.add(Ingredient.fromJson(o));
                }
            } else {
                ring.add(Ingredient.fromJson(ringArr.get(i)));
            }
        }

        ItemStack result = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("result"));
        int haloTier = GsonHelper.getAsInt(json, "required_halo_tier", 0);
        String displayKey = GsonHelper.getAsString(json, "display_type");

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
