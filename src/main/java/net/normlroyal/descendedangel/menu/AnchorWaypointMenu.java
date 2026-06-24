package net.normlroyal.descendedangel.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.normlroyal.descendedangel.content.dimension.AnchorTeleportCost;
import net.normlroyal.descendedangel.content.dimension.VoidPocketManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnchorWaypointMenu extends AbstractContainerMenu {
    private final BlockPos sourcePos;
    private final List<Entry> entries;

    public AnchorWaypointMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, buf.readBlockPos(), readEntries(buf));
    }

    public AnchorWaypointMenu(int id, Inventory playerInventory, BlockPos sourcePos, List<Entry> entries) {
        super(ModMenus.ANCHOR_WAYPOINT_MENU.get(), id);
        this.sourcePos = sourcePos;
        this.entries = List.copyOf(entries);
    }

    public BlockPos getSourcePos() {
        return sourcePos;
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public boolean stillValid(Player player) {
        return !player.isSpectator()
                && player.distanceToSqr(
                sourcePos.getX() + 0.5D,
                sourcePos.getY() + 0.5D,
                sourcePos.getZ() + 0.5D
        ) <= 64.0D;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        if (id < 0 || id >= entries.size()) {
            return false;
        }

        Entry entry = entries.get(id);
        VoidPocketManager.teleportBetweenAnchors(serverPlayer, sourcePos, entry.dimension(), entry.pos());
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    public static void writeEntries(FriendlyByteBuf buf, List<Entry> entries) {
        buf.writeVarInt(entries.size());
        for (Entry entry : entries) {
            entry.write(buf);
        }
    }

    public static List<Entry> readEntries(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Entry> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            entries.add(Entry.read(buf));
        }
        return entries;
    }

    public record Entry(
            ResourceKey<Level> dimension,
            BlockPos pos,
            String name,
            String dimensionLabel,
            String distanceLabel,
            boolean voidPocket,
            AnchorTeleportCost cost
    ) {
        public static Entry read(FriendlyByteBuf buf) {
            ResourceLocation dimensionId = buf.readResourceLocation();
            BlockPos pos = buf.readBlockPos();
            String name = buf.readUtf(128);
            String dimensionLabel = buf.readUtf(64);
            String distanceLabel = buf.readUtf(64);
            boolean voidPocket = buf.readBoolean();
            AnchorTeleportCost cost = AnchorTeleportCost.read(buf);
            return new Entry(
                    ResourceKey.create(Registries.DIMENSION, dimensionId),
                    pos,
                    name,
                    dimensionLabel,
                    distanceLabel,
                    voidPocket,
                    cost
            );
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeResourceLocation(dimension.location());
            buf.writeBlockPos(pos);
            buf.writeUtf(name, 128);
            buf.writeUtf(dimensionLabel, 64);
            buf.writeUtf(distanceLabel, 64);
            buf.writeBoolean(voidPocket);
            cost.write(buf);
        }
    }
}
