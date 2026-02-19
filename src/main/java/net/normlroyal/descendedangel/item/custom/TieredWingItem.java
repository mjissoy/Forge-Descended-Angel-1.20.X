package net.normlroyal.descendedangel.item.custom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.normlroyal.descendedangel.events.useful.WingRenderContext;
import net.normlroyal.descendedangel.util.IWingItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.caelus.api.CaelusApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class TieredWingItem extends Item implements GeoItem, IWingItem, ICurioItem {

    private static final UUID CAELUS_FALL_FLY_UUID =
            UUID.fromString("7b2adf4e-1f43-4d83-9c3f-3d5e1c28b3f1");

    private final int tier;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public TieredWingItem(int tier, Properties props) {
        super(props);
        this.tier = tier;
    }

    public int getTier() { return tier; }
    @Override public int wingTier() { return tier; }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (tier != 1) return;

        LivingEntity e = slotContext.entity();
        enableCaelusFallFlying(e);   // run on client + server
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (tier != 1) return;

        LivingEntity e = slotContext.entity();
        disableCaelusFallFlying(e);  // run on client + server
    }

    private static void enableCaelusFallFlying(LivingEntity e) {
        var api = CaelusApi.getInstance();

        var attr = api.getFlightAttribute();
        var inst = e.getAttribute(attr);
        if (inst == null) return;

        AttributeModifier elytraMod = api.getElytraModifier();
        if (inst.getModifier(elytraMod.getId()) == null) {
            inst.addTransientModifier(elytraMod);
        }
    }

    private static void disableCaelusFallFlying(LivingEntity e) {
        var api = CaelusApi.getInstance();

        var attr = api.getFlightAttribute();
        var inst = e.getAttribute(attr);
        if (inst == null) return;

        AttributeModifier elytraMod = api.getElytraModifier();
        if (inst.getModifier(elytraMod.getId()) != null) {
            inst.removeModifier(elytraMod.getId());
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            LivingEntity e = WingRenderContext.getEntity();
            if (e == null) return PlayState.STOP;

            var c = state.getController();
            String prefix = "animation.wing_t" + tier + ".";

            if (e.isFallFlying()) {
                c.setAnimation(RawAnimation.begin().thenLoop(prefix + "flying"));
                return PlayState.CONTINUE;
            }

            if (!e.onGround()) {
                c.setAnimation(RawAnimation.begin().thenLoop(prefix + "open_idle"));
            } else {
                c.setAnimation(RawAnimation.begin().thenLoop(prefix + "closed_idle"));
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new net.minecraftforge.client.extensions.common.IClientItemExtensions() {
            private net.normlroyal.descendedangel.client.render.WingItemRenderer renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new net.normlroyal.descendedangel.client.render.WingItemRenderer();
                }
                return this.renderer;
            }
        });
    }

}
