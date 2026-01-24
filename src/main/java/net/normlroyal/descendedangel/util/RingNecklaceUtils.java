package net.normlroyal.descendedangel.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.item.custom.enums.NecklaceVariants;
import net.normlroyal.descendedangel.item.custom.enums.RingVariants;
import top.theillusivec4.curios.api.CuriosApi;

public final class RingNecklaceUtils {
    private RingNecklaceUtils() {}

    public static boolean isFromMod(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem())
                .getNamespace().equals("descendedangel");
    }

    public static boolean hasVariant(ItemStack stack) {
        return stack.getItem() instanceof IVariantItem<?>;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T getVariant(ItemStack stack, Class<T> enumClass) {
        if (!(stack.getItem() instanceof IVariantItem<?> variantItem)) return null;
        Enum<?> variant = ((IVariantItem<?>) variantItem).getVariant(stack);
        return enumClass.isInstance(variant) ? (T) variant : null;
    }

    public static <T extends Enum<T>> T getFirstVariantFromCurioSlot(Player player, String slotId, Class<T> enumClass) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(inv -> inv.getCurios().get(slotId))
                .map(handler -> {
                    var stacks = handler.getStacks();
                    for (int i = 0; i < stacks.getSlots(); i++) {
                        ItemStack stack = stacks.getStackInSlot(i);
                        if (stack.isEmpty()) continue;
                        if (!isFromMod(stack) || !hasVariant(stack)) continue;

                        T variant = getVariant(stack, enumClass);
                        if (variant != null) return variant;
                    }
                    return (T) null;
                })
                .orElse(null);
    }

    public static <T extends Enum<T>> boolean hasVariantInCurioSlot(Player player, String slotId, Class<T> enumClass, T wanted) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(inv -> inv.getCurios().get(slotId))
                .map(handler -> {
                    var stacks = handler.getStacks();
                    for (int i = 0; i < stacks.getSlots(); i++) {
                        ItemStack stack = stacks.getStackInSlot(i);
                        if (stack.isEmpty()) continue;
                        if (!isFromMod(stack) || !hasVariant(stack)) continue;

                        T variant = getVariant(stack, enumClass);
                        if (variant == wanted) return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    public static <T extends Enum<T>> int countVariantInCurioSlot(Player player, String slotId, Class<T> enumClass, T wanted) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(inv -> inv.getCurios().get(slotId))
                .map(handler -> {
                    int count = 0;
                    var stacks = handler.getStacks();
                    for (int i = 0; i < stacks.getSlots(); i++) {
                        ItemStack stack = stacks.getStackInSlot(i);
                        if (stack.isEmpty()) continue;
                        if (!isFromMod(stack) || !hasVariant(stack)) continue;

                        T variant = getVariant(stack, enumClass);
                        if (variant == wanted) count++;
                    }
                    return count;
                })
                .orElse(0);
    }

    public static RingVariants getEquippedRing(Player player) {
        return getFirstVariantFromCurioSlot(player, "ring", RingVariants.class);
    }

    public static boolean hasRing(Player player, RingVariants variant) {
        return hasVariantInCurioSlot(player, "ring", RingVariants.class, variant);
    }

    public static int countRing(Player player, RingVariants variant) {
        return countVariantInCurioSlot(player, "ring", RingVariants.class, variant);
    }

    public static NecklaceVariants getEquippedNecklace(Player player) {
        return getFirstVariantFromCurioSlot(player, "necklace", NecklaceVariants.class);
    }

    public static boolean hasNecklace(Player player, NecklaceVariants variant) {
        return hasVariantInCurioSlot(player, "necklace", NecklaceVariants.class, variant);
    }
}
