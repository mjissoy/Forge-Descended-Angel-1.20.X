package net.normlroyal.descendedangel.content.recipe;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.normlroyal.descendedangel.content.block.ModBlocks;
import net.normlroyal.descendedangel.content.item.ModItems;
import net.normlroyal.descendedangel.potions.ModPotions;

public class ModBrewingRecipes {
    public static void register(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BrewingRecipeRegistry.addRecipe(new PotionBrewingRecipe(
                    Potions.STRONG_HEALING,
                    Ingredient.of(ModItems.SACRED_BLOOD.get()),
                    ModPotions.DIVINE_GRACE.get()
            ));

            BrewingRecipeRegistry.addRecipe(new PotionBrewingRecipe(
                    ModPotions.DIVINE_GRACE.get(),
                    Ingredient.of(Items.GLOWSTONE_DUST),
                    ModPotions.STRONG_DIVINE_GRACE.get()
            ));

            BrewingRecipeRegistry.addRecipe(new PotionBrewingRecipe(
                    Potions.AWKWARD,
                    Ingredient.of(ModItems.ANGELS_TEARS.get()),
                    ModPotions.PROVIDENCE.get()
            ));

            BrewingRecipeRegistry.addRecipe(new PotionBrewingRecipe(
                    ModPotions.PROVIDENCE.get(),
                    Ingredient.of(Items.REDSTONE),
                    ModPotions.LONG_PROVIDENCE.get()
            ));

            BrewingRecipeRegistry.addRecipe(new PotionBrewingRecipe(
                    ModPotions.PROVIDENCE.get(),
                    Ingredient.of(Items.GLOWSTONE_DUST),
                    ModPotions.STRONG_PROVIDENCE.get()
            ));

            BrewingRecipeRegistry.addRecipe(new PotionBrewingRecipe(
                    Potions.POISON,
                    Ingredient.of(ModBlocks.ANGEL_WEEPING.get()),
                    ModPotions.LOCUST_SWARM.get()
            ));

            BrewingRecipeRegistry.addRecipe(new PotionBrewingRecipe(
                    ModPotions.LOCUST_SWARM.get(),
                    Ingredient.of(Items.REDSTONE),
                    ModPotions.LONG_LOCUST_SWARM.get()
            ));

            BrewingRecipeRegistry.addRecipe(new PotionBrewingRecipe(
                    ModPotions.LOCUST_SWARM.get(),
                    Ingredient.of(Items.GLOWSTONE_DUST),
                    ModPotions.STRONG_LOCUST_SWARM.get()
            ));

            BrewingRecipeRegistry.addRecipe(new PotionBrewingRecipe(
                    ModPotions.PROVIDENCE.get(),
                    Ingredient.of(ModItems.SACRED_BLOOD.get()),
                    ModPotions.BLESSED_FAVOUR.get()
            ));

            BrewingRecipeRegistry.addRecipe(new PotionBrewingRecipe(
                    ModPotions.BLESSED_FAVOUR.get(),
                    Ingredient.of(Items.REDSTONE),
                    ModPotions.LONG_BLESSED_FAVOUR.get()
            ));
        });
    }
}