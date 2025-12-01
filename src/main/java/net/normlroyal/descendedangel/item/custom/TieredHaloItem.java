package net.normlroyal.descendedangel.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class TieredHaloItem extends Item implements ICurioItem {

    private final int tier; // 1..9

    public TieredHaloItem(Properties props, int tier) {
        super(props);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            UUID uuid,
            ItemStack stack
    ) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

        double extraHealth = 2 + (tier - 1) * tier;
        double extraArmor  = 5 + (tier - 1) * tier;

        builder.put(
                Attributes.MAX_HEALTH,
                new AttributeModifier(
                        UUID.nameUUIDFromBytes(("halo_health_" + tier).getBytes()),
                        "halo_health_bonus_t" + tier,
                        extraHealth,
                        AttributeModifier.Operation.ADDITION
                )
        );

        builder.put(
                Attributes.ARMOR,
                new AttributeModifier(
                        UUID.nameUUIDFromBytes(("halo_armor_" + tier).getBytes()),
                        "halo_armor_bonus_t" + tier,
                        extraArmor,
                        AttributeModifier.Operation.ADDITION
                )
        );

        return builder.build();
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        super.appendHoverText(stack, level, tooltip, flag);


        int bonusPercent = tier * 10;

        tooltip.add(Component.empty()); // blank line

        tooltip.add(
                Component.translatable("tooltip.descendedangel.halo.when_worn")
                        .withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(
                Component.translatable("tooltip.descendedangel.halo.undead_damage", bonusPercent)
                        .withStyle(ChatFormatting.YELLOW)
        );

        tooltip.add(
                Component.translatable("tooltip.descendedangel.halo.healing_bonus", bonusPercent)
                        .withStyle(ChatFormatting.YELLOW)
        );
    }

}
