package net.normlroyal.descendedangel.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
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
        if (!matchesIgnoringNBT(core, container.getItem(8))) {
            return false;
        }

        for (int i = 0; i < 8; i++) {
            Ingredient ing = ring.get(i);
            ItemStack stack = container.getItem(i);

            if (!matchesIgnoringNBT(ing, stack)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesIgnoringNBT(Ingredient ing, ItemStack stack) {
        if (ing.isEmpty()) {
            return stack.isEmpty();
        }

        if (stack.isEmpty()) {
            return false;
        }

        for (ItemStack example : ing.getItems()) {
            if (!example.isEmpty() && example.getItem() == stack.getItem()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess access) {
        ItemStack out = result.copy();

        ItemStack potionStack = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (!s.isEmpty() && PotionUtils.getPotion(s) != null && PotionUtils.getPotion(s) != net.minecraft.world.item.alchemy.Potions.EMPTY) {
                potionStack = s;
                break;
            }
        }

        if (!potionStack.isEmpty()) {
            Potion p = PotionUtils.getPotion(potionStack);
            var potionKey = BuiltInRegistries.POTION.getKey(p);

            CompoundTag tag = out.getOrCreateTag();
            CompoundTag root = tag.contains("descendedangel", Tag.TAG_COMPOUND) ? tag.getCompound("descendedangel") : new CompoundTag();
            root.putString("boost_potion", potionKey.toString());
            tag.put("descendedangel", root);
        }

        return out;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return result.copy();
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

