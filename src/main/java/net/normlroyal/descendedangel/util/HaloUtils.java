package net.normlroyal.descendedangel.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.item.custom.TieredHaloItem;
import net.normlroyal.descendedangel.tags.ModItemTags;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

public class HaloUtils {

    public static Optional<ItemStack> findEquippedHalo(Player player) {
        return CuriosApi.getCuriosHelper()
                .findFirstCurio(player, stack -> stack.is(ModItemTags.HALOS))
                .map(slotResult -> slotResult.stack());
    }

    public static int getHaloTier(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof TieredHaloItem haloItem) {
            return haloItem.getTier();
        }
        return 0;
    }

    public static int getEquippedHaloTier(Player player) {
        return findEquippedHalo(player)
                .map(HaloUtils::getHaloTier)
                .orElse(0);
    }
}
