package net.normlroyal.descendedangel.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.normlroyal.descendedangel.block.altar.AltarBlockEntity;

public class AltarMenu extends AbstractContainerMenu {

    private final ContainerData data;

    public final AltarBlockEntity blockEntity;

    public AltarMenu(int id, Inventory playerInv, AltarBlockEntity be) {
        super(ModMenus.ALTAR_MENU.get(), id);
        this.blockEntity = be;

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> be.getProgress();
                    case 1 -> be.getMaxProgress();
                    case 2 -> be.isCrafting() ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (be.getLevel() != null && be.getLevel().isClientSide) {
                    switch (index) {
                        case 0 -> be.setClientProgress(value);
                        case 1 -> be.setClientMaxProgress(value);
                        case 2 -> be.setClientCrafting(value != 0);
                    }
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };

        this.addDataSlots(this.data);

        ItemStackHandler inv = be.getItems();

        // Ring slots 0-7
        addSlot(new LockedSlot(be, inv, 0, 82, 28));    // top
        addSlot(new LockedSlot(be, inv, 1, 105, 38));  // top-right
        addSlot(new LockedSlot(be, inv, 2, 115, 61));  // right
        addSlot(new LockedSlot(be, inv, 3, 105, 84));  // bottom-right
        addSlot(new LockedSlot(be, inv, 4, 82, 94));   // bottom
        addSlot(new LockedSlot(be, inv, 5, 58, 84));   // bottom-left
        addSlot(new LockedSlot(be, inv, 6, 48, 61));   // left
        addSlot(new LockedSlot(be, inv, 7, 58, 38));   // top-left

        addSlot(new LockedSlot(be, inv, AltarBlockEntity.CORE_SLOT, 82, 61));

        addSlot(new HaloSlot(be, inv, AltarBlockEntity.HALO_SLOT, 82, 7));

        addPlayerInventory(playerInv);
        addPlayerHotbar(playerInv);
    }

    public AltarMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getBE(playerInv, buf));
    }

    private static AltarBlockEntity getBE(Inventory playerInv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        var be = playerInv.player.level().getBlockEntity(pos);
        if (!(be instanceof AltarBlockEntity altar)) {
            throw new IllegalStateException("Altar block entity not found at " + pos);
        }
        return altar;
    }

    @Override
    public boolean stillValid(Player player) {
        return !player.isSpectator()
                && player.distanceToSqr(
                blockEntity.getBlockPos().getX() + 0.5,
                blockEntity.getBlockPos().getY() + 0.5,
                blockEntity.getBlockPos().getZ() + 0.5
        ) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return empty;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        final int ALTAR_SLOTS = AltarBlockEntity.SLOT_COUNT;
        final int PLAYER_INV_START = ALTAR_SLOTS;
        final int PLAYER_INV_END = PLAYER_INV_START + 27;
        final int HOTBAR_END = PLAYER_INV_END + 9;

        if (index < ALTAR_SLOTS) {
            if (!this.moveItemStackTo(stack, PLAYER_INV_START, HOTBAR_END, true)) return empty;
        } else {
            if (this.slots.get(AltarBlockEntity.HALO_SLOT).mayPlace(stack)) {
                if (!this.moveItemStackTo(stack, AltarBlockEntity.HALO_SLOT, AltarBlockEntity.HALO_SLOT + 1, false))
                    return empty;
            } else {
                if (!this.moveItemStackTo(stack, 0, AltarBlockEntity.CORE_SLOT + 1, false)) return empty;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return copy;
    }

    private void addPlayerInventory(Inventory playerInv) {
        int yStart = 165;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9,
                        9 + col * 18,
                        yStart + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInv) {
        int yHotbar = 223;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 9 + col * 18, yHotbar));
        }
    }

    public static final int BTN_START_RITE = 0;

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == BTN_START_RITE) {
            if (!player.level().isClientSide) {
                blockEntity.tryStartRite((ServerPlayer) player);
            }
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    public int getProgress() { return data.get(0); }
    public int getMaxProgress() { return data.get(1); }
    public boolean isCrafting() { return data.get(2) != 0; }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
    }

}