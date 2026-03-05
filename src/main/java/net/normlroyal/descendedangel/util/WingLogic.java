package net.normlroyal.descendedangel.util;

import net.minecraft.world.item.ItemStack;

public final class WingLogic {

    private WingLogic() {}

    public static int getWingTier(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        return (stack.getItem() instanceof IWingItem w) ? w.wingTier() : 0;
    }

    public static boolean allowsCustomFlight(ItemStack stack) {
        return getWingTier(stack) >= 2;
    }
}
