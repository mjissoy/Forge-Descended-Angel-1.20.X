package net.normlroyal.descendedangel.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.normlroyal.descendedangel.item.ModItems;

import javax.annotation.Nullable;
import java.util.List;

public class MarkOfCainItem extends Item {

    private static final String CHARGE_TAG = "Charge";

    private static final int CHARGE_PER_USE = 81;

    public MarkOfCainItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    public static int getCharge(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return 0;
        return Math.max(0, tag.getInt(CHARGE_TAG));
    }

    public static void setCharge(ItemStack stack, int charge) {
        if (!(stack.getItem() instanceof MarkOfCainItem markItem)) return;

        int clamped = Math.max(0, Math.min(charge, markItem.getMaxCharge(stack)));
        stack.getOrCreateTag().putInt(CHARGE_TAG, clamped);
    }

    public static void addCharge(ItemStack stack, int amount) {
        if (amount <= 0) return;
        setCharge(stack, getCharge(stack) + amount);
    }

    public static void removeCharge(ItemStack stack, int amount) {
        if (amount <= 0) return;
        setCharge(stack, getCharge(stack) - amount);
    }

    public int getMaxCharge(ItemStack stack) {
        return CHARGE_PER_USE * getMaxUses(stack);
    }

    public int getMaxUses(ItemStack stack) {
        return 1;
    }

    public int getStoredUses(ItemStack stack) {
        return getCharge(stack) / CHARGE_PER_USE;
    }

    public boolean canPopTotem(ItemStack stack) {
        return getCharge(stack) >= CHARGE_PER_USE;
    }

    public boolean consumeOneUse(ItemStack stack) {
        if (!canPopTotem(stack)) return false;
        removeCharge(stack, CHARGE_PER_USE);
        return true;
    }

    public int getChargeNeededForNextUse(ItemStack stack) {
        int remainder = getCharge(stack) % CHARGE_PER_USE;
        return remainder == 0 ? 0 : CHARGE_PER_USE - remainder;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack carriedStack, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.PRIMARY && action != ClickAction.SECONDARY) return false;

        ItemStack targetStack = slot.getItem();
        int chargeValue = getChargeValue(targetStack);

        if (chargeValue <= 0) return false;
        if (!canAcceptCharge(carriedStack, chargeValue)) return false;

        if (!player.level().isClientSide) {
            addCharge(carriedStack, chargeValue);
            targetStack.shrink(1);
            slot.setChanged();
        }

        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack markStack, ItemStack carriedStack, Slot slot,
                                            ClickAction action, Player player, SlotAccess access) {
        if (action != ClickAction.PRIMARY && action != ClickAction.SECONDARY) return false;

        int chargeValue = getChargeValue(carriedStack);
        if (chargeValue <= 0) return false;
        if (!canAcceptCharge(markStack, chargeValue)) return false;

        if (!player.level().isClientSide) {
            addCharge(markStack, chargeValue);
            carriedStack.shrink(1);
            slot.setChanged();
        }

        return true;
    }

    private int getChargeValue(ItemStack stack) {
        if (stack.is(ModItems.VOIDTEAR.get())) {
            return 1;
        }
        if (stack.is(ModItems.COMPRESSEDVOID.get())) {
            return 9;
        }
        if (stack.is(ModItems.VOIDMATRIX.get())) {
            return 81;
        }
        return 0;
    }

    public boolean canAcceptCharge(ItemStack markStack, int amount) {
        return amount > 0 && getCharge(markStack) + amount <= getMaxCharge(markStack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getCharge(stack) >= 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int charge = getCharge(stack);
        int max = getMaxCharge(stack);
        if (max <= 0) return 0;

        return Math.round(13.0f * charge / max);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x7A00FF;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return canPopTotem(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int charge = getCharge(stack);
        int max = getMaxCharge(stack);
        int uses = getStoredUses(stack);

        if (Screen.hasShiftDown()) {

            tooltip.add(
                    Component.translatable("tooltip.descendedangel.mark_of_cain_lore")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)
            );
        } else {
            tooltip.add(
                    Component.translatable("tooltip.descendedangel.halo.hold_shift")
                            .withStyle(ChatFormatting.DARK_GRAY)
            );
        }

        tooltip.add(Component.empty());

        tooltip.add(Component.literal("Stored Charge: " + charge + " / " + max));
        tooltip.add(Component.literal("Stored Uses: " + uses));

        if (!canPopTotem(stack)) {
            tooltip.add(Component.translatable("tooltip.descendedangel.mark_of_cain_not_ready"));
        } else {
            tooltip.add(Component.translatable("tooltip.descendedangel.mark_of_cain_ready"));
        }
    }
}

