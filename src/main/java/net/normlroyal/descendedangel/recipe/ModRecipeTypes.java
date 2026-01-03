package net.normlroyal.descendedangel.recipe;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.normlroyal.descendedangel.DescendedAngel;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, DescendedAngel.MOD_ID);

    public static final RegistryObject<RecipeType<AltarRiteRecipe>> ALTAR_RITE =
            TYPES.register("altar_rite", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return DescendedAngel.MOD_ID + ":altar_rite";
                }
            });
}