package net.normlroyal.descendedangel.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.block.ModBlocks;
import net.normlroyal.descendedangel.recipe.AltarRiteRecipe;
import net.normlroyal.descendedangel.recipe.ModRecipeTypes;

@JeiPlugin
public class DescendedAngelJeiPlugin implements IModPlugin {

    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "jei_plugin");

    public static final RecipeType<AltarRiteRecipe> ALTAR_RITE =
            new RecipeType<>(ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, "altar_rite"), AltarRiteRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new AltarRiteCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        var recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.ALTAR_RITE.get());
        registration.addRecipes(ALTAR_RITE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ALTAR.get()), ALTAR_RITE);
    }
}