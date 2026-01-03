package net.normlroyal.descendedangel.menu;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.normlroyal.descendedangel.block.altar.AltarBlockEntity;
import net.normlroyal.descendedangel.item.custom.TieredHaloItem;

public class HaloSlot extends SlotItemHandler {
    private final AltarBlockEntity be;

    public HaloSlot(AltarBlockEntity be, IItemHandler handler, int index, int x, int y) {
        super(handler, index, x, y);
        this.be = be;
    }

    @Override
    public boolean mayPlace(net.minecraft.world.item.ItemStack stack) {
        if (be.isCrafting()) return false;
        return stack.getItem() instanceof TieredHaloItem;
    }

    @Override
    public boolean mayPickup(net.minecraft.world.entity.player.Player player) {
        return !be.isCrafting();
    }
}
