package net.normlroyal.descendedangel.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.normlroyal.descendedangel.config.ModConfigs;
import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class TieredHaloItem extends Item implements ICurioItem, GeoItem {

    private final int tier;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

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

        double t = tier;

        double extraHealth = ModConfigs.COMMON.HALO_HEALTH_BASE.get()
                + ModConfigs.COMMON.HALO_HEALTH_MULTI.get() * (t - 1) * t;

        double extraArmor = ModConfigs.COMMON.HALO_ARMOR_BASE.get()
                + ModConfigs.COMMON.HALO_ARMOR_MULTI.get() * (t - 1) * t;

        double global = ModConfigs.COMMON.HALO_EFFECTIVENESS_MULTIPLIER.get();
        extraHealth *= global;
        extraArmor  *= global;

        UUID healthUuid = UUID.nameUUIDFromBytes((uuid.toString() + ":halo_health").getBytes());
        UUID armorUuid  = UUID.nameUUIDFromBytes((uuid.toString() + ":halo_armor").getBytes());

        builder.put(
                Attributes.MAX_HEALTH,
                new AttributeModifier(
                        healthUuid,
                        "halo_health_bonus_t" + tier,
                        extraHealth,
                        AttributeModifier.Operation.ADDITION
                )
        );

        builder.put(
                Attributes.ARMOR,
                new AttributeModifier(
                        armorUuid,
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


        double healPerTierper = ModConfigs.COMMON.HALO_HEAL_BONUS_PER_TIER.get() * 100.0;
        double dmgPerTierper = ModConfigs.COMMON.HALO_UNDEAD_DAMAGE_BONUS_PER_TIER.get() * 100.0;

        double healbon  = healPerTierper * tier;
        double undbon  = dmgPerTierper * tier;


        if (Screen.hasShiftDown()) {

            tooltip.add(
                    Component.translatable("tooltip.descendedangel.halo_t" + tier + ".lore")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)
            );
        } else {
            tooltip.add(
                    Component.translatable("tooltip.descendedangel.halo.hold_shift")
                            .withStyle(ChatFormatting.DARK_GRAY)
            );
        }

        tooltip.add(Component.empty());

        tooltip.add(
                Component.translatable("tooltip.descendedangel.halo.when_worn")
                        .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(
                Component.translatable("tooltip.descendedangel.halo.undead_damage", undbon)
                        .withStyle(ChatFormatting.BLUE));

        tooltip.add(
                Component.translatable("tooltip.descendedangel.halo.healing_bonus", healbon)
                        .withStyle(ChatFormatting.BLUE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private net.normlroyal.descendedangel.client.render.HaloItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new net.normlroyal.descendedangel.client.render.HaloItemRenderer();
                }
                return this.renderer;
            }
        });
    }
}
