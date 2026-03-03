package net.normlroyal.descendedangel.item.custom;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.normlroyal.descendedangel.client.render.DestinySpearRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;
import java.util.function.Consumer;

public class DestinySpearItem extends SwordItem implements GeoItem {
    private static final UUID REACH_UUID = UUID.fromString("3b1b0f6a-6a5d-4c79-a6a6-1e1f7a4b3d11");

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", state -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private DestinySpearRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new DestinySpearRenderer();
                return renderer;
            }
        });
    }

}