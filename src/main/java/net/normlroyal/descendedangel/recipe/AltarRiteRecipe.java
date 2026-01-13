package net.normlroyal.descendedangel.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public class AltarRiteRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final Ingredient core;
    private final List<Ingredient> ring;
    private final ItemStack result;
    private final int requiredHaloTier;
    private final String displayTypeKey;
    public String displayTypeKey() { return displayTypeKey; }


    public AltarRiteRecipe(
            ResourceLocation id,
            Ingredient core,
            List<Ingredient> ring,
            ItemStack result,
            int requiredHaloTier,
            String displayTypeKey
    ) {
        this.id = id;
        this.core = core;
        this.ring = ring;
        this.result = result;
        this.requiredHaloTier = requiredHaloTier;
        this.displayTypeKey = displayTypeKey;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {

        if (!core.test(container.getItem(8))) return false;

        for (int i = 0; i < 8; i++) {
            if (!ring.get(i).test(container.getItem(i))) {
                return false;
            }
        }
        return true;

    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess access) {
        return result.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return result;
    }

    @Override public boolean canCraftInDimensions(int w, int h) { return true; }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return ModRecipeSerializers.ALTAR_RITE.get(); }
    @Override public RecipeType<?> getType() { return ModRecipeTypes.ALTAR_RITE.get(); }

    public int requiredHaloTier() { return requiredHaloTier; }
    public Component displayComponent() {
        return Component.translatable(displayTypeKey);
    }

    public Ingredient getCore() {
        return core;
    }

    public List<Ingredient> getRing() {
        return ring;
    }

    public ItemStack getResult() {
        return result;
    }

}

