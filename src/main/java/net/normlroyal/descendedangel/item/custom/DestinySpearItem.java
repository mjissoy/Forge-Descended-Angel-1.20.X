package net.normlroyal.descendedangel.item.custom;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeMod;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.UUID;

public class DestinySpearItem extends SwordItem {
    private static final UUID REACH_UUID = UUID.fromString("3b1b0f6a-6a5d-4c79-a6a6-1e1f7a4b3d11");

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public DestinySpearItem(Tier tier, int attackDamageBonus, float attackSpeed, Item.Properties props) {
        super(tier, attackDamageBonus, attackSpeed, props);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder =
                ImmutableMultimap.builder();

        builder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND));

        builder.put(ForgeMod.ENTITY_REACH.get(),
                new AttributeModifier(
                        REACH_UUID,
                        "Spear reach",
                        1.5D,
                        AttributeModifier.Operation.ADDITION
                )
        );

        this.defaultModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND
                ? this.defaultModifiers
                : super.getDefaultAttributeModifiers(slot);
    }

}