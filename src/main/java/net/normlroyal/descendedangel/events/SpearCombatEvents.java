package net.normlroyal.descendedangel.events;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.item.custom.DestinySpearItem;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpearCombatEvents {

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (level.isClientSide) return;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof DestinySpearItem)) return;

        float strength = player.getAttackStrengthScale(0.0F);
        if (strength < 0.65F) return;

        boolean wantsSweep = player.isShiftKeyDown();

        boolean didCustom = wantsSweep
                ? doSweep(player, stack)
                : (player.getRandom().nextBoolean() ? doThrust(player, stack) : doCut(player, stack));

        if (didCustom) event.setCanceled(true);
    }

    private static boolean doThrust(Player player, ItemStack spear) {
        LivingEntity target = raytraceLiving(player, 4.6D, 1.0D);
        if (target == null) return false;

        float base = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float dmg = base + 2.0F;

        if (!target.hurt(player.damageSources().playerAttack(player), dmg)) return false;

        player.level().playSound(null, player.blockPosition(),
                SoundEvents.TRIDENT_HIT, SoundSource.PLAYERS, 1.0F, 1.2F);

        Vec3 dir = player.getLookAngle().normalize();
        player.push(dir.x * 0.18, 0.0, dir.z * 0.18);
        player.hurtMarked = true;

        Vec3 kb = dir.scale(0.65);
        target.push(kb.x, 0.08, kb.z);

        if (player.level() instanceof ServerLevel sl) {
            spawnThrustLine(sl, player, 4.6D);
        }

        spear.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        player.getCooldowns().addCooldown(spear.getItem(), 3);

        return true;
    }

    private static boolean doCut(Player player, ItemStack spear) {
        LivingEntity target = raytraceLiving(player, 3.2D, 0.8D);
        if (target == null) return false;

        float base = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float dmg = base + 0.5F;

        if (!target.hurt(player.damageSources().playerAttack(player), dmg)) return false;

        player.level().playSound(null, player.blockPosition(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);

        if (player.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.CRIT,
                    target.getX(), target.getY() + 1.0D, target.getZ(),
                    6, 0.25D, 0.25D, 0.25D, 0.05D);

        }

        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 50, 0));

        spear.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        player.getCooldowns().addCooldown(spear.getItem(), 1);

        return true;
    }

    private static boolean doSweep(Player player, ItemStack spear) {
        Level level = player.level();

        double radius = 3.0D;
        Vec3 origin = player.position().add(0, 1.0, 0);
        Vec3 forward = player.getLookAngle().normalize();
        AABB area = player.getBoundingBox().inflate(radius, 1.0, radius);

        float base = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float dmg = base * 0.75F;

        int hits = 0;
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, area,
                ent -> ent != player && ent.isAlive() && ent.isPickable())) {

            Vec3 to = e.position().add(0, 1.0, 0).subtract(origin).normalize();
            if (forward.dot(to) < 0.35) continue;

            if (e.hurt(player.damageSources().playerAttack(player), dmg)) {
                Vec3 push = forward.scale(0.35);
                e.push(push.x, 0.05, push.z);
                hits++;
            }
        }

        if (hits <= 0) return false;

        if (player.level() instanceof ServerLevel sl) {
        }

        spear.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        player.getCooldowns().addCooldown(spear.getItem(), 14);

        return true;
    }

    private static LivingEntity raytraceLiving(Player player, double range, double inflate) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(range));

        AABB box = player.getBoundingBox().expandTowards(look.scale(range)).inflate(inflate);

        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
                player.level(), player, eye, end, box,
                e -> e instanceof LivingEntity le && le.isAlive() && e.isPickable() && e != player
        );

        return (hit != null && hit.getEntity() instanceof LivingEntity le) ? le : null;
    }

    private static void spawnThrustLine(ServerLevel level, Player player, double range) {
        Vec3 eye = player.getEyePosition();
        Vec3 dir = player.getLookAngle().normalize();
        Vec3 start = eye.add(dir.scale(0.5));

        int points = 14;
        for (int i = 1; i <= points; i++) {
            double t = (range * i) / points;
            Vec3 p = start.add(dir.scale(t));

            level.sendParticles(ParticleTypes.END_ROD,
                    p.x, p.y, p.z, 1,
                    0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}