package net.normlroyal.descendedangel.content.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.normlroyal.descendedangel.DescendedAngel;

public class ModDimensions {
    public static final ResourceKey<Level> VOID_POCKET_LEVEL = ResourceKey.create(
            Registries.DIMENSION,
            new ResourceLocation(DescendedAngel.MOD_ID, "void_pocket")
    );

    public static final ResourceKey<DimensionType> VOID_POCKET_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            new ResourceLocation(DescendedAngel.MOD_ID, "void_pocket")
    );

    private ModDimensions() {
    }
}
