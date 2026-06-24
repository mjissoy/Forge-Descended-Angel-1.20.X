package net.normlroyal.descendedangel.content.dimension;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.content.item.ModItems;

public record AnchorTeleportCost(int voidTears, int compressedVoid, int voidMatrix) {
    public static final AnchorTeleportCost FREE = new AnchorTeleportCost(0, 0, 0);

    public static AnchorTeleportCost read(FriendlyByteBuf buf) {
        return new AnchorTeleportCost(buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(voidTears);
        buf.writeVarInt(compressedVoid);
        buf.writeVarInt(voidMatrix);
    }

    public boolean canAfford(ServerPlayer player) {
        if (player.isCreative()) {
            return true;
        }

        return count(player, ModItems.VOIDTEAR.get()) >= voidTears
                && count(player, ModItems.COMPRESSEDVOID.get()) >= compressedVoid
                && count(player, ModItems.VOIDMATRIX.get()) >= voidMatrix;
    }

    public void consume(ServerPlayer player) {
        if (player.isCreative()) {
            return;
        }

        remove(player, ModItems.VOIDTEAR.get(), voidTears);
        remove(player, ModItems.COMPRESSEDVOID.get(), compressedVoid);
        remove(player, ModItems.VOIDMATRIX.get(), voidMatrix);
        player.getInventory().setChanged();
    }

    public String label() {
        if (voidTears <= 0 && compressedVoid <= 0 && voidMatrix <= 0) {
            return "Free";
        }

        StringBuilder builder = new StringBuilder();
        append(builder, voidTears, "Void Droplet");
        append(builder, compressedVoid, "Void Sphere");
        append(builder, voidMatrix, "Void Matrix");
        return builder.toString();
    }

    public String shortLabel() {
        if (voidTears <= 0 && compressedVoid <= 0 && voidMatrix <= 0) {
            return "Free";
        }

        StringBuilder builder = new StringBuilder();
        appendShort(builder, voidTears, "VD");
        appendShort(builder, compressedVoid, "VS");
        appendShort(builder, voidMatrix, "VM");
        return builder.toString();
    }

    private static void append(StringBuilder builder, int count, String name) {
        if (count <= 0) {
            return;
        }
        if (!builder.isEmpty()) {
            builder.append(", ");
        }
        builder.append(count).append(' ').append(name);
        if (count != 1 && !name.endsWith("Matrix")) {
            builder.append('s');
        }
    }

    private static void appendShort(StringBuilder builder, int count, String name) {
        if (count <= 0) {
            return;
        }
        if (!builder.isEmpty()) {
            builder.append("  ");
        }
        builder.append(count).append(' ').append(name);
    }

    private static int count(ServerPlayer player, Item item) {
        int total = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(item)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static void remove(ServerPlayer player, Item item, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.is(item)) {
                continue;
            }

            int taken = Math.min(remaining, stack.getCount());
            stack.shrink(taken);
            remaining -= taken;
        }
    }
}
