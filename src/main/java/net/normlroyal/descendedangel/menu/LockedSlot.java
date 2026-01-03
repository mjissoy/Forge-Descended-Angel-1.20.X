package net.normlroyal.descendedangel.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.normlroyal.descendedangel.block.altar.AltarBlockEntity;

public class LockedSlot extends SlotItemHandler {
    private final AltarBlockEntity be;

    public LockedSlot(AltarBlockEntity be, IItemHandler handler, int index, int x, int y) {
        super(handler, index, x, y);
        this.be = be;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !be.isCrafting();
    }

    @Override
    public boolean mayPickup(Player player) {
        return !be.isCrafting();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}

