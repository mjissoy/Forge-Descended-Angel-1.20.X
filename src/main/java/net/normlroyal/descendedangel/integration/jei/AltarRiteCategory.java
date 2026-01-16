package net.normlroyal.descendedangel.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.ModItems;
import net.normlroyal.descendedangel.recipe.AltarRiteRecipe;

import java.util.ArrayList;
import java.util.List;

public class AltarRiteCategory implements IRecipeCategory<AltarRiteRecipe> {

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(DescendedAngel.MOD_ID, path);
    }

    private static final ResourceLocation BG = rl("textures/gui/jei_altar_workbench.png");

    private final IDrawable background;
    private final IDrawable icon;

    public AltarRiteCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BG, 0, 0, 177, 117);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.REALANGELFEATHER.get()));
    }

    @Override
    public mezz.jei.api.recipe.RecipeType<AltarRiteRecipe> getRecipeType() {
        return DescendedAngelJeiPlugin.ALTAR_RITE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.descendedangel.altar_rite");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AltarRiteRecipe recipe, mezz.jei.api.recipe.IFocusGroup focuses) {

        int[][] ringPos = new int[][]{
                {60, 28},  //  top
                {83, 38}, //  top-right
                {93, 61}, //  right
                {83, 84}, //  bottom-right
                {60, 94},  //  bottom
                {36, 84},  //  bottom-left
                {26, 61},  //  left
                {36, 38}   //  top-left
        };

        List<net.minecraft.world.item.crafting.Ingredient> ring = recipe.getRing();
        for (int i = 0; i < 8; i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, ringPos[i][0], ringPos[i][1])
                    .addIngredients(ring.get(i));
        }

        // Centre
        builder.addSlot(RecipeIngredientRole.INPUT, 60, 61)
                .addIngredients(recipe.getCore());

        // Output
        var level = Minecraft.getInstance().level;
        ItemStack out = (level != null)
                ? recipe.getResultItem(level.registryAccess()).copy()
                : recipe.getResult().copy();

        builder.addSlot(RecipeIngredientRole.OUTPUT, 134, 61)
                .addItemStack(out);

        int min = recipe.requiredHaloTier();
        int max = 9;

        var halos = new ArrayList<ItemStack>();
        for (int t = min; t <= max; t++) {
            halos.add(getHaloStackForTier(t));
        }

        builder.addSlot(RecipeIngredientRole.CATALYST, 60, 7)
                .addItemStacks(halos)
                .addTooltipCallback((view, tooltip) -> {
                    tooltip.clear();
                    tooltip.add(Component.translatable("altar_jei.halo_t"+ min + ".name"));
                });
    }

    @Override
    public void draw(AltarRiteRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics g, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;

        Component text = recipe.displayComponent();
        int w = font.width(text);

        int bgWidth = 177;
        int x = bgWidth - 6 - w;
        int y = 6;

        g.drawString(font, text, x, y, 0x404040, false);
    }

    private static ItemStack getHaloStackForTier(int tier) {
        return switch (tier) {
            case 1 -> new ItemStack(ModItems.HALO_T1.get());
            case 2 -> new ItemStack(ModItems.HALO_T2.get());
            case 3 -> new ItemStack(ModItems.HALO_T3.get());
            case 4 -> new ItemStack(ModItems.HALO_T4.get());
            case 5 -> new ItemStack(ModItems.HALO_T5.get());
            case 6 -> new ItemStack(ModItems.HALO_T6.get());
            case 7 -> new ItemStack(ModItems.HALO_T7.get());
            case 8 -> new ItemStack(ModItems.HALO_T8.get());
            case 9 -> new ItemStack(ModItems.HALO_T9.get());
            default -> new ItemStack(ModItems.HALO_T1.get());
        };
    }
}
