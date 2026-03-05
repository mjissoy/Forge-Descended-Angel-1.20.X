package net.normlroyal.descendedangel.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public class WingUtils {

    public static final String WINGS_SLOT = "wing";

    private WingUtils() {}

    public static ItemStack getEquippedWings(Player player) {
        var invOpt = CuriosApi.getCuriosInventory(player).resolve();
        if (invOpt.isEmpty()) return ItemStack.EMPTY;

        var handlerOpt = invOpt.get().getStacksHandler(WINGS_SLOT);
        if (handlerOpt.isEmpty()) return ItemStack.EMPTY;

        var stacks = handlerOpt.get().getStacks();
        return stacks.getStackInSlot(0);
    }

    public static boolean hasWings(Player player) {
        return !getEquippedWings(player).isEmpty();
    }
}
