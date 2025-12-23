package net.normlroyal.descendedangel.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.config.DANBTS.WingNBTs;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.item.custom.TieredWingItem;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

public class WingUtils {
    private WingUtils() {}


    public static int getWingTier(Player player) {
        return CuriosApi.getCuriosHelper()
                .findFirstCurio(player, stack -> stack.getItem() instanceof TieredWingItem)
                .map(found -> ((TieredWingItem) found.stack().getItem()).getTier())
                .orElse(0);
    }

    public static Optional<ItemStack> getWingStack(Player player) {
        return CuriosApi.getCuriosHelper()
                .findFirstCurio(player, stack -> stack.getItem() instanceof TieredWingItem)
                .map(found -> found.stack());
    }


    public static double getOrInitT3Speed(Player player) {
        var nbt = player.getPersistentData();
        if (!nbt.contains(WingNBTs.T3_SPEED)) {
            double def = ModConfigs.COMMON.T3_DEFAULT_SPEED.get();
            nbt.putDouble(WingNBTs.T3_SPEED, def);
            return def;
        }
        return nbt.getDouble(WingNBTs.T3_SPEED);
    }

    public static void setT3Speed(Player player, double speed) {
        player.getPersistentData().putDouble(WingNBTs.T3_SPEED, speed);
    }
}