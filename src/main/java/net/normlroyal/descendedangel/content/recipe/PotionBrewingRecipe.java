package net.normlroyal.descendedangel.content.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class PotionBrewingRecipe implements IBrewingRecipe {
    private final Potion inputPotion;
    private final Ingredient ingredient;
    private final Potion outputPotion;

    public PotionBrewingRecipe(Potion inputPotion, Ingredient ingredient, Potion outputPotion) {
        this.inputPotion = inputPotion;
        this.ingredient = ingredient;
        this.outputPotion = outputPotion;
    }

    @Override
    public boolean isInput(ItemStack stack) {
        boolean validBottle = stack.is(Items.POTION)
                || stack.is(Items.SPLASH_POTION)
                || stack.is(Items.LINGERING_POTION);

        return validBottle && PotionUtils.getPotion(stack) == this.inputPotion;
    }

    @Override
    public boolean isIngredient(ItemStack stack) {
        return this.ingredient.test(stack);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (!this.isInput(input) || !this.isIngredient(ingredient)) {
            return ItemStack.EMPTY;
        }

        ItemStack output = new ItemStack(input.getItem());
        PotionUtils.setPotion(output, this.outputPotion);
        return output;
    }
}