package net.normlroyal.descendedangel.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.normlroyal.descendedangel.DescendedAngel;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, DescendedAngel.MOD_ID);

    public static final RegistryObject<RecipeSerializer<?>> ALTAR_RITE =
            SERIALIZERS.register("altar_rite", AltarRiteRecipeSerializer::new);
}
