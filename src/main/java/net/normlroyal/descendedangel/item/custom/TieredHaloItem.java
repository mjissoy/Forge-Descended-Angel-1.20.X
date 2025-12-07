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

    private final int tier; // 1..9

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

        double extraHealth = 2 + (tier - 1) * tier;
        double extraArmor = 5 + (tier - 1) * tier;

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
        int healbonusPercent = tier * 5;

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
                        .withStyle(ChatFormatting.DARK_GRAY)
        );

        tooltip.add(
                Component.translatable("tooltip.descendedangel.halo.undead_damage", bonusPercent)
                        .withStyle(ChatFormatting.BLUE)
        );

        tooltip.add(
                Component.translatable("tooltip.descendedangel.halo.healing_bonus", healbonusPercent)
                        .withStyle(ChatFormatting.BLUE)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    // Forge hook to attach the GeoItemRenderer
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
