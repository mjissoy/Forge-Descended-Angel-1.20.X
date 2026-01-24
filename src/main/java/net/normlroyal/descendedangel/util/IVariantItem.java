package net.normlroyal.descendedangel.util;

import net.minecraft.world.item.ItemStack;

public interface IVariantItem<T extends Enum<T>> {
    T getVariant(ItemStack stack);
}

