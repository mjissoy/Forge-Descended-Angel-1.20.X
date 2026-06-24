package net.normlroyal.descendedangel.content.item.custom;

import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.common.config.ModConfigs;

public class PurifiedMarkOfCainItem extends MarkOfCainItem {

    public PurifiedMarkOfCainItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getMaxUses(ItemStack stack) {
        return ModConfigs.COMMON.MAX_PURIFIED_MARK_USES.get();
    }

}
