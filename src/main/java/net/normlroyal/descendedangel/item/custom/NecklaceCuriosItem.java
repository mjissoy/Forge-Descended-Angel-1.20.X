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
import net.normlroyal.descendedangel.item.custom.enums.NecklaceVariants;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class NecklaceCuriosItem extends Item implements ICurioItem {
    private final NecklaceVariants variant;

    public NecklaceCuriosItem(NecklaceVariants variant, Properties props) {
        super(props);
        this.variant = variant;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext, UUID uuid, ItemStack stack) {

        if (variant == NecklaceVariants.HOLY) {
            return ImmutableMultimap.of();
        }

        if (variant == NecklaceVariants.MESSENGER) {
            return ImmutableMultimap.of(
                    Attributes.LUCK,
                    new AttributeModifier(uuid, "descendedangel:messenger_necklace_luck",
                            0.30, AttributeModifier.Operation.MULTIPLY_TOTAL)
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

        if (variant == NecklaceVariants.MESSENGER) {
            if (Screen.hasShiftDown()) {

                tooltip.add(
                        Component.translatable("tooltip.descendedangel.messenger_pendant.lore")
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
