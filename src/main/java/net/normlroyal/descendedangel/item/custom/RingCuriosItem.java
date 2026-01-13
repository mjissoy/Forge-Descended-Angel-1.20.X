package net.normlroyal.descendedangel.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.normlroyal.descendedangel.item.custom.enums.RingVariants;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class RingCuriosItem extends Item implements ICurioItem {
    private final RingVariants variant;

    public RingCuriosItem(RingVariants variant, Properties props) {
        super(props);
        this.variant = variant;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext, UUID uuid, ItemStack stack) {

        if (variant == RingVariants.HOLY) {
            return ImmutableMultimap.of();
        }

        if (variant == RingVariants.CLOUD) {
            return ImmutableMultimap.of(
                    Attributes.MOVEMENT_SPEED,
                    new AttributeModifier(uuid, "descendedangel:cloud_ring_speed",
                            0.20, AttributeModifier.Operation.MULTIPLY_TOTAL),
                    Attributes.ATTACK_SPEED,
                    new AttributeModifier(uuid, "descendedangel:cloud_ring_attackspeed",
                            0.1, AttributeModifier.Operation.MULTIPLY_TOTAL)
            );
        }

        return ImmutableMultimap.of();
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Level level,
            List<Component> tooltip,
            TooltipFlag flag) {

        super.appendHoverText(stack, level, tooltip, flag);

        if (variant == RingVariants.CLOUD) {
            if (Screen.hasShiftDown()) {

                tooltip.add(
                        Component.translatable("tooltip.descendedangel.cloud_ring.lore")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)
                );
            } else {
                tooltip.add(
                        Component.translatable("tooltip.descendedangel.halo.hold_shift")
                                .withStyle(ChatFormatting.DARK_GRAY)
                );
            }
        }
    }

}
