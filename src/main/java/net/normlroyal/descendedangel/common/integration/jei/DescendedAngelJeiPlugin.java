package net.normlroyal.descendedangel.common.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.content.block.ModBlocks;
import net.normlroyal.descendedangel.content.item.ModItems;
import net.normlroyal.descendedangel.content.recipe.AltarRiteRecipe;
import net.normlroyal.descendedangel.content.recipe.ModRecipeTypes;
import net.normlroyal.descendedangel.potions.ModPotions;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class DescendedAngelJeiPlugin implements IModPlugin {

    public static final ResourceLocation UID =
            new ResourceLocation(DescendedAngel.MOD_ID, "jei_plugin");

    public static final RecipeType<AltarRiteRecipe> ALTAR_RITE =
            new RecipeType<>(new ResourceLocation(DescendedAngel.MOD_ID, "altar_rite"), AltarRiteRecipe.class);

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

        if (level != null) {
            var recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.ALTAR_RITE.get());
            registration.addRecipes(ALTAR_RITE, recipes);
        };

        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();

        List<IJeiBrewingRecipe> brewingRecipes = new ArrayList<>();

        brewingRecipes.add(createBrewingRecipe(
                factory,
                "divine_grace",
                ModItems.SACRED_BLOOD.get().getDefaultInstance(),
                potionStack(Potions.STRONG_HEALING),
                potionStack(ModPotions.DIVINE_GRACE.get())
        ));

        brewingRecipes.add(createBrewingRecipe(
                factory,
                "providence",
                ModItems.ANGELS_TEARS.get().getDefaultInstance(),
                potionStack(Potions.AWKWARD),
                potionStack(ModPotions.PROVIDENCE.get())
        ));

        brewingRecipes.add(createBrewingRecipe(
                factory,
                "locust_swarm",
                ModBlocks.ANGEL_WEEPING.get().asItem().getDefaultInstance(),
                potionStack(Potions.POISON),
                potionStack(ModPotions.LOCUST_SWARM.get())
        ));

        brewingRecipes.add(createBrewingRecipe(
                factory,
                "blessed_favour",
                ModItems.SACRED_BLOOD.get().getDefaultInstance(),
                potionStack(ModPotions.PROVIDENCE.get()),
                potionStack(ModPotions.BLESSED_FAVOUR.get())
        ));

        registration.addRecipes(RecipeTypes.BREWING, brewingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ALTAR.get()), ALTAR_RITE);
    }

    private static IJeiBrewingRecipe createBrewingRecipe(
            IVanillaRecipeFactory factory,
            String name,
            ItemStack ingredient,
            ItemStack input,
            ItemStack output
    ) {
        return factory.createBrewingRecipe(
                List.of(ingredient),
                input,
                output,
                new ResourceLocation(DescendedAngel.MOD_ID, "brewing/" + name)
        );
    }

    private static ItemStack potionStack(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }
}